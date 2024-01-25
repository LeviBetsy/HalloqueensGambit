package halloqueensgambit.java;
import halloqueensgambit.java.piece.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import java.nio.file.Path;
import java.nio.file.Paths;

import static halloqueensgambit.java.IO.scanPiece;


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

    public boolean hasBothKing(){
        int sum = 0;
        for (var entry : this.board){
            Piece piece = entry.getValue();
            if (piece instanceof King){
                sum++;
            }
        }
        return sum == 2;
    }


    //this function will return true if at THIS BOARD BUT OPPONENT'S TURN, there is a move to take king
    //for checking stalemate, bc stalemate means you are safe rn but there are no move which endanger you.
    public boolean kingIsChecked(){
        Game falseGame = new Game(this.side.enemy(), this.board);
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
            Board nextBoard = this.board.makeMove(m);
            if (this.side == Side.BLACK){
                nextGames.add(new Game(Side.WHITE, nextBoard));
            } else {
                nextGames.add(new Game(Side.BLACK, nextBoard));
            }
        }
        return nextGames;
    }

    public int evaluateBoard(){
        return board.evaluate();
    }

    //TODO: Stringbuilder
    @Override
    public String toString(){
        String result = "";
        result += "Current player: " + this.side.toString() + "\n";
        result += this.board.toString();
        return result;
    }
}
