package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;

public class Game{
    private Side side;
    private Board board;

    public Side getSide(){
        return this.side;
    }

    public Board getBoard(){
        return this.board;
    }

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
        
        this.side = Side.WHITE;
        if(lines[1].equals("b")){this.side = Side.BLACK;}
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
            Game tmp = this.copy();
            tmp.makeMove(m);
            nextGames.add(tmp);
        }
        return nextGames;
    }

    public int evaluateBoard(){
        return board.evaluate();
    }

    public ArrayList<Move> getLegalMoves(){
        ArrayList<Move> legalMoves = new ArrayList<>();
        //iterate through board entries, i.e. pos/piece tuples
        for(var entry: this.board){ 
            if(entry.getValue().side() == this.side){
                // get the moves for this piece and add to the big arraylist
                ArrayList<Move> movesForThisPiece = entry.getValue().allLegalMove(entry.getKey(), this.board); // ugly af
                legalMoves.addAll(movesForThisPiece);
            }
        }
        return legalMoves;
    }

    public Game copy(){
        return new Game(this.side, this.board);
    }

    @Override
    public String toString(){
        String result = "";
        result += "Current player: " + this.side.toString() + "\n";
        result += this.board.toString();
        return result;
    }

    public boolean isCheck(){
        FogartySolver.moveRating bestMove = FogartySolver.exhaustive(new Game(Side.opponent(this.side), this.board), 1);
        return Math.abs(bestMove.rating()) > 1000;
    }
    
    public boolean isCheckMate(){
        FogartySolver.moveRating bestMove = FogartySolver.exhaustive(new Game(this.side, this.board), 2);
        return Math.abs(bestMove.rating()) > 1000;
    }

    public void makeMove(Move move){
        this.side = Side.opponent(side);
        Piece movingPiece = this.board.data.remove(move.start);
        if (movingPiece instanceof King){
            ((King) movingPiece).hasMoved = true;
        } else if (movingPiece instanceof Rook){
            ((Rook) movingPiece).hasMoved = true;
        }

        //PROMOTION
        if (movingPiece instanceof Pawn) {
            if (movingPiece.side() == Side.WHITE && move.start.y() == 7 && move.end.y() == 8){
                this.board.data.put(move.end, new Queen(Side.WHITE));
            } else if (movingPiece.side() == Side.BLACK && move.start.y() == 2 && move.end.y() == 1){
                this.board.data.put(move.end, new Queen(Side.BLACK));
            } else {
                this.board.data.put(move.end, movingPiece);
            }
        } 
        //TODO: KING HASMOVED
        //CASTLING
        else if (movingPiece instanceof King) {
            //CASTLING WHITE QUEEN SIDE
            if (movingPiece.side() == Side.WHITE && move.start.equals(new Pos(5,1) )
                    && move.end.equals(new Pos(3,1))){
                //MOVING THE ROOK
                this.board.data.remove(new Pos(1,1));
                this.board.data.put(new Pos(4,1), new Rook(Side.WHITE, true));
                this.board.data.put(move.end, movingPiece);
            //CASTLING WHITE KING SIDE
            } else if (movingPiece.side() == Side.WHITE && move.start.equals(new Pos(5,1) )
                    && move.end.equals(new Pos(7,1))){
                //MOVING THE ROOK
                this.board.data.remove(new Pos(8,1));
                this.board.data.put(new Pos(6,1), new Rook(Side.WHITE, true));
                this.board.data.put(move.end, movingPiece);
            //CASTLING BLACK QUEEN SIDE
            } else if (movingPiece.side() == Side.BLACK && move.start.equals(new Pos(5,8) )
                    && move.end.equals(new Pos(3,8))){
                //MOVING THE ROOK
                this.board.data.remove(new Pos(1,8));
                this.board.data.put(new Pos(4,8), new Rook(Side.BLACK, true));
                this.board.data.put(move.end, movingPiece);
            //CASTLING BLACK KING SIDE
            } else if (movingPiece.side() == Side.BLACK && move.start.equals(new Pos(5,8) )
                    && move.end.equals(new Pos(7,8))) {
                //MOVING THE ROOK
                this.board.data.remove(new Pos(8, 8));
                this.board.data.put(new Pos(6, 8), new Rook(Side.BLACK, true));
                this.board.data.put(move.end, movingPiece);
            } else {
                this.board.data.put(move.end, movingPiece);
            }
        } else {
            this.board.data.put(move.end, movingPiece);
        }
    }

    // TODO: TESTING
    public void unMakeMove(Move move){
        this.side = Side.opponent(side);
        Piece movingPiece = this.board.data.remove(move.end);

        // check promotions
        if(move.isPromotion){
            this.board.data.put(move.start, new Pawn(movingPiece.side()));
            return;
        }
        
        // put the moving piece back where it goes
        this.board.data.put(move.start, movingPiece);

        // handle captures
        if(move.pieceCaptured.isPresent()){
            this.board.data.put(move.end, move.pieceCaptured.get());
        }

        // check castles
        if(movingPiece instanceof King){
            // if the king moves more than one space, it must have castled
            if(move.getDistance() > 1){
                // put the white king rook back
                if(move.end.x() == 7 && move.end.y == 1){
                    Piece kingRook = this.board.data.remove(new Pos(6, 1));
                    this.board.data.put(new Pos(8, 1), kingRook);
                }
                // put the white queen rook back
                else if(move.end.x() == 3 && move.end.y == 1){
                    Piece kingRook = this.board.data.remove(new Pos(4, 1));
                    this.board.data.put(new Pos(1, 1), kingRook);
                }
                // put the black king rook back
                else if(move.end.x() == 7 && move.end.y == 8){
                    Piece kingRook = this.board.data.remove(new Pos(6, 8));
                    this.board.data.put(new Pos(8, 8), kingRook);
                }
                // put the black queen rook back
                else if(move.end.x() == 3 && move.end.y == 8){
                    Piece kingRook = this.board.data.remove(new Pos(4, 8));
                    this.board.data.put(new Pos(8, 1), kingRook);
                }
            }
        }
    }
}
