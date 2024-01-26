package halloqueensgambit.java;
import halloqueensgambit.java.piece.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeMap;

public class Game {
    private Board board;
    public Side getSide(){
        return this.side;
    }
    public Board getBoard(){
        return this.board;
    }
    private Side side;
    private boolean castleWhiteKingside;
    private boolean castleWhiteQueenside;
    private boolean castleBlackKingside;
    private boolean castleBlackQueenside;
    private Pos enPassantTargetSquare;
    private int halfmoveClock;
    private int turnCounter;


    public Game(Side side, Board board){
        this.side = side;
        this.board = board;
    }

    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
    public Game(String fen){
        // split the string appropriately
        String[] lines = fen.split(" ");
        String[] rows = lines[0].split("/");

        this.board = new Board();
        
        HashMap<Character, Integer> digits = new HashMap<>();
        for(int i = 49; i <= 56; i ++){
            digits.put((char) i, i-48);
        }

        System.out.println(digits);

        // read the board
        for(int current_row = 1; current_row <= 8; current_row ++){
            int current_column = 1;
            for(char c: rows[8-current_row].toCharArray()){
                //check digit
                if(digits.containsKey(c)){
                    current_column += digits.get(c);
                }else{
                    Piece p = IO.scanPiece(Character.toString(c)).get();
                    this.board.addToBoard(new Game.Pos(current_column, current_row), p);
                    current_column ++;
                }
            }
        }

        System.out.println(this.board.data);

        // read the side to move
        this.side = Side.WHITE;
        if(lines[1] == "b"){
            this.side = Side.BLACK;
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
            if (entry.getValue() instanceof King){
                sum++;
            }
            if (sum == 2)
                return true;
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

    public ArrayList<Move> getLegalMoves(){
        ArrayList<Move> legalMoves = new ArrayList<>();
        //iterate through board entries, i.e. pieces
        for(var entry: this.board){ 
            if(entry.getValue().side() == this.side){
                // get the moves for this piece and add to the big arraylist
                ArrayList<Move> movesForThisPiece = entry.getValue().allLegalMove(entry.getKey(), this.board); // ugly af
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
