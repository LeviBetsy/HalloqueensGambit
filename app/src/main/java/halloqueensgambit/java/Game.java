package halloqueensgambit.java;
import halloqueensgambit.java.piece.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Game {
    private Side side;
    private Board board;

    public Game(Side side, Board board){
        this.side = side;
        this.board = board;
    }

    //constructor for creating game from file name
    //file must be inside of games folder
    public Game(String fileName){
        try{
            // Resolve the file path
            String currentDirectory = System.getProperty("user.dir");
            // Connect the filepath
            Path filePath = Paths.get(currentDirectory, "src/main/java/halloqueensgambit/java/games", fileName);
            Scanner scanner = new Scanner(filePath);

            this.side = (scanner.nextLine().equals("B")) ? Side.BLACK : Side.WHITE;
            this.board = new Board();

            for (int y = 8; y >= 1; y--){
                String row = scanner.nextLine();
                //splitting a row into individual squares
                String[] squares = row.split("\\s+");
                for (int x = 1; x <= 8; x++){
                    Optional<Piece> currentPiece = scanPiece(squares[x - 1], new Pos(x,y));
                    //if scan Piece does not return an Optional value
                    if (currentPiece.isPresent()){
                        this.board.addToBoard(new Pos(x,y), currentPiece.get());
                    }
                }
            }
            scanner.close();
        } catch(Exception e){
            System.out.println("Unable to read board: " + e.getLocalizedMessage());
        }
    }


    /*                           DATATYPE                           */
    public static record Pos(int x, int y) implements Comparable<Pos> {
        //allow comparing equals and less or larger for tree tracing
        @Override
        public int compareTo(Pos other) {
            int xComparison = Integer.compare(this.x, other.x);
            if (xComparison != 0) {
                return xComparison;
            }
            return Integer.compare(this.y, other.y);
        }
    }

    //keeping track of the piece after the move so we can make move easier in board
    //TODO: think of not doing that for less overhead space
    public static record Move(Pos start, Pos end){};
    public static record OffSet(int dx, int dy){};

    /*                                METHODS                                */

    public Board getBoard(){
        return this.board;
    }

    //RETURN THE SIDE WHICH HAS TAKEN THE OPPONENT'S KING
    public Optional<Side> whoHasWon(){
        boolean hasWhiteKing = false;
        boolean hasBlackKing = false;
        for (var entry : this.board){
            Piece piece = entry.getValue();
            if (piece.side() == Side.WHITE && piece instanceof King)
                hasWhiteKing = true;
            if (piece.side() == Side.BLACK && piece instanceof King)
                hasBlackKing = true;
        }
        if (hasWhiteKing && hasBlackKing){
            return Optional.empty();
        } else if (hasWhiteKing){
            return Optional.of(Side.WHITE);
        } else {
            return Optional.of(Side.BLACK);
        }
    }


    //this function will return true if at THIS BOARD BUT OPPONENT'S TURN, there is a move to take king
    //for checking stalemate, bc stalemate means you are safe rn but there are no move which endanger you.
    public boolean kingIsChecked(){
        for (var entry : this.board) {
            Piece piece = entry.getValue();
            Pos pos = entry.getKey();
            if (piece.side() == this.side){
                var allMove = piece.allLegalMove(pos, this.board);
                for (Move m : allMove){
//                    if ()
                }
            }
        }
        return false;
    }

    public static boolean inBound(Game.Pos pos){
        return (pos.x() >= 1 && pos.x() <= 8 && pos.y() >= 1 && pos.y() <= 8);
    }

    public ArrayList<Game> allNextGames(){
        ArrayList<Move> allLegalMoves = new ArrayList<>();
        for (var entry : this.board) {
            Piece piece = entry.getValue();
            Pos pos = entry.getKey();
            if (piece.side() == this.side){
                allLegalMoves.addAll(piece.allLegalMove(pos, this.board));
            }
        }

        ArrayList<Game> nextGames = new ArrayList<>();
        for (Move m : allLegalMoves){
            nextGames.add(this.makeMove(m));
        }
        return nextGames;
    }

    public int evaluateBoard(){
        return board.evaluate();
    }

    public ArrayList<Game.Move> getLegalMoves(){
        ArrayList<Game.Move> legalMoves = new ArrayList<Game.Move>();
        //iterate through board entries, i.e. pieces
        for(var entry: this.board){ 
            if(entry.getValue().side() == side){
                // get the moves for this piece and add to the big arraylist
                ArrayList<Game.Move> movesForThisPiece = entry.getValue().allLegalMove(entry.getKey(), this.board); // ugly af
                legalMoves.addAll(movesForThisPiece);
            }
        }
        return legalMoves;
    }

    //TODO: Stringbuilder
    @Override
    public String toString(){
        String result = "";
        result += "Current player: " + this.side.toString() + "\n";
        result += this.board.toString();
        return result;
    }

    // TODO: consider where this should really go
    private static Optional<Piece> scanPiece(String c, Pos pos){
        return switch (c) {
            case "R" -> Optional.of(new Rook(Side.WHITE,false));
            case "r" -> Optional.of(new Rook(Side.BLACK,false));
            case "N" -> Optional.of(new Knight(Side.WHITE));
            case "n" -> Optional.of(new Knight(Side.BLACK));
            case "B" -> Optional.of(new Bishop(Side.WHITE));
            case "b" -> Optional.of(new Bishop(Side.BLACK));
            case "K" -> Optional.of(new King(Side.WHITE,false));
            case "k" -> Optional.of(new King(Side.BLACK, false));
            case "Q" -> Optional.of(new Queen(Side.WHITE));
            case "q" -> Optional.of(new Queen(Side.BLACK));
            case "P" -> Optional.of(new Pawn(Side.WHITE));
            case "p" -> Optional.of(new Pawn(Side.BLACK));
            default -> Optional.empty();
        };
    }

    public Game makeMove(Move move){
        TreeMap<Pos, Piece> newData = new TreeMap<>(board.data);
        Piece movingPiece = newData.remove(move.start());
        if (movingPiece instanceof King){
            ((King) movingPiece).hasMoved = true;
        } else if (movingPiece instanceof Rook){
            ((Rook) movingPiece).hasMoved = true;
        }

        //PROMOTION
        if (movingPiece instanceof Pawn) {
            if (movingPiece.side() == Side.WHITE && move.start().y() == 7 && move.end().y() == 8){
                newData.put(move.end(), new Queen(Side.WHITE));
            } else if (movingPiece.side() == Side.BLACK && move.start().y() == 2 && move.end().y() == 1){
                newData.put(move.end(), new Queen(Side.BLACK));
            } else {
                newData.put(move.end(), movingPiece);
            }
        } 
        //CASTLING
        else if (movingPiece instanceof King) {
            //CASTLING WHITE QUEEN SIDE
            if (movingPiece.side() == Side.WHITE && move.start().equals(new Pos(5,1) )
                    && move.end().equals(new Pos(3,1))){
                //MOVING THE ROOK
                newData.remove(new Pos(1,1));
                newData.put(new Pos(4,1), new Rook(Side.WHITE, true));
                newData.put(move.end(), movingPiece);
            //CASTLING WHITE KING SIDE
            } else if (movingPiece.side() == Side.WHITE && move.start().equals(new Pos(5,1) )
                    && move.end().equals(new Pos(7,1))){
                //MOVING THE ROOK
                newData.remove(new Pos(8,1));
                newData.put(new Pos(6,1), new Rook(Side.WHITE, true));
                newData.put(move.end(), movingPiece);
            //CASTLING BLACK QUEEN SIDE
            } else if (movingPiece.side() == Side.BLACK && move.start().equals(new Pos(5,8) )
                    && move.end().equals(new Pos(3,8))){
                //MOVING THE ROOK
                newData.remove(new Pos(1,8));
                newData.put(new Pos(4,8), new Rook(Side.BLACK, true));
                newData.put(move.end(), movingPiece);
            //CASTLING BLACK KING SIDE
            } else if (movingPiece.side() == Side.BLACK && move.start().equals(new Pos(5,8) )
                    && move.end().equals(new Pos(7,8))) {
                //MOVING THE ROOK
                newData.remove(new Pos(8, 8));
                newData.put(new Pos(6, 8), new Rook(Side.BLACK, true));
                newData.put(move.end(), movingPiece);
            } else {
                newData.put(move.end(), movingPiece);
            }
        } else {
            newData.put(move.end(), movingPiece);
        }
        return new Game(Side.opponent(side), new Board(newData));
    }

    public Game unMakeMove(Move move){
        return this;
    }
}
