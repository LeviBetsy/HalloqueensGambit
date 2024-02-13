package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;

public class Game{
    private Side side;
    private Board board;
    private int evaluation;

    /*                           SETTERS, GETTERS, CONSTRUCTORS                           */
    public Side side(){
        return this.side;
    }
    public int evaluation() {return this.evaluation;}

    public Board board(){
        return this.board;
    }

    public Game(Side side, Board board){
        this.side = side;
        this.board = board;
        this.evaluation = board.evaluate();
    }

    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
    public Game(String fen){
        // split the string appropriately
        String[] lines = fen.split(" ");

        this.board = Board.fromFEN(lines[0]);

        this.side = Side.WHITE;
        if(lines[1].equals("b")){
            this.side = Side.BLACK;
        }

        this.evaluation = board.evaluate();
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

    public static boolean inBound(Game.Pos pos){
        return (pos.x() >= 1 && pos.x() <= 8 && pos.y() >= 1 && pos.y() <= 8);
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

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Game)) return false;
        Game other = (Game) obj;
        return this.side.equals(other.side) && this.board.equals(other.board);
    }

    public Optional<Piece> makeMove(Move move){
        Optional<Piece> captured = this.board.lookup(move.end);

        this.side = Side.opponent(side);
        Piece movingPiece = this.board.data.remove(move.start);

        if (movingPiece instanceof King){
            ((King) movingPiece).hasMoved = true;
        } else if (movingPiece instanceof Rook){
            ((Rook) movingPiece).hasMoved = true;
        }

        //PROMOTION
        if (movingPiece instanceof Pawn) {
            if (movingPiece.side() == Side.WHITE && move.end.y() == 8){
                Queen newQueen = new Queen(Side.WHITE);
                this.board.data.put(move.end, newQueen);
                //handling evaluation
                this.evaluation -= movingPiece.value();
                this.evaluation += newQueen.value();
            } else if (movingPiece.side() == Side.BLACK && move.end.y() == 1){
                Queen newQueen = new Queen(Side.BLACK);
                this.board.data.put(move.end, newQueen);
                //handling evaluation
                this.evaluation -= movingPiece.value();
                this.evaluation += newQueen.value();
            } else {
                this.board.data.put(move.end, movingPiece);
            }
        } 

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

        captured.ifPresent(piece -> this.evaluation -= piece.value());
        return captured;
    }

    public void unMakeMove(Move move, Optional<Piece> captured){
        this.side = Side.opponent(side);
        Piece movingPiece = this.board.data.remove(move.end);
        
        // handle captures AND promotions
        if(captured.isPresent()) {
            this.board.data.put(move.end, captured.get());
            this.evaluation += captured.get().value();
        }

        // put the moving piece back where it goes
        this.board.data.put(move.start, movingPiece);

        // check promotions
        if(move.isPromotion){
            Pawn pawn = new Pawn(this.side);
            this.board.data.put(move.start, pawn);
            //handling evaluation
            this.evaluation += pawn.value();
            this.evaluation -= movingPiece.value();
        }

        // check castles
        if(movingPiece instanceof King){
            // if the king moves more than one space, it must have castled
            if(move.getDistance() > 1){
                System.out.println("Distance greater than 1, castling must have occurred");
                // put the white king rook back
                if(move.end.x() == 7 && move.end.y == 1){
                    System.out.println("Uncastle white kingside");
                    Piece rook = this.board.data.remove(new Pos(6, 1));
                    this.board.data.put(new Pos(8, 1), rook);
                }
                // put the white queen rook back
                else if(move.end.x() == 3 && move.end.y == 1){
                    System.out.println("Uncastle white queenside");
                    Piece rook = this.board.data.remove(new Pos(4, 1));
                    this.board.data.put(new Pos(1, 1), rook);
                }
                // put the black king rook back
                else if(move.end.x() == 7 && move.end.y == 8){
                    System.out.println("Uncastle black kingside");
                    Piece rook = this.board.data.remove(new Pos(6, 8));
                    this.board.data.put(new Pos(8, 8), rook);
                }
                // put the black queen rook back
                else if(move.end.x() == 3 && move.end.y == 8){
                    System.out.println("Uncastle black queenside");
                    Piece rook = this.board.data.remove(new Pos(4, 8));
                    this.board.data.put(new Pos(1, 8), rook);
                }
            }
        }
    }
}
