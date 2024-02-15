package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;

import java.util.*;

public class Game{
    private Side side;
    private Board board;

    //TODO: update king positions every time you make Move and unmakeMove
    private Pos wKing;
    private Pos bKing;
    private int evaluation;
    static char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    /*                           SETTERS, GETTERS, CONSTRUCTORS                           */
    public Side side(){
        return this.side;
    }
    public int evaluation() {return this.evaluation;}

    public Board board(){
        return this.board;
    }

    public Pos getCurrentKing(){
        return (this.side == Side.WHITE) ? wKing : bKing;
    }

    public Game(Side side, Board board){
        this.side = side;
        this.board = board;
        this.evaluation = board.evaluate();
        this.wKing = board.findKing(Side.WHITE);
        this.bKing = board.findKing(Side.BLACK);
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
        this.wKing = board.findKing(Side.WHITE);
        this.bKing = board.findKing(Side.BLACK);
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

        @Override
        public final String toString() {
            return letters[x-1] + "" + y;
        }
    }

    public static record OffSet(int dx, int dy){};

    public static final OffSet[] allOffset = {
            new Game.OffSet(0, 1),
            new Game.OffSet(0, -1),
            new Game.OffSet(1, 0),
            new Game.OffSet(-1, 0),
            new Game.OffSet(1, 1),
            new Game.OffSet(1, -1),
            new Game.OffSet(-1, 1),
            new Game.OffSet(-1, -1)
    };

    public static final OffSet[] diagonalOffset = {
            new Game.OffSet(1, 1),
            new Game.OffSet(1, -1),
            new Game.OffSet(-1, 1),
            new Game.OffSet(-1, -1)
    };

    public static final OffSet[] straightOffset = {
            new Game.OffSet(0, 1),
            new Game.OffSet(0, -1),
            new Game.OffSet(1, 0),
            new Game.OffSet(-1, 0)
    };

    /*                                METHODS                                */

    public static boolean inBound(Game.Pos pos){
        return (pos.x() >= 1 && pos.x() <= 8 && pos.y() >= 1 && pos.y() <= 8);
    }

    public Set<Pos> dangerousSquare(){
        Set<Pos> result = new HashSet<>();
        for (var entry : this.board){
            Piece p = entry.getValue();
            if (p.side() == Side.opponent(this.side)){
                p.addControllingSquares(result, entry.getKey(), this.board);
            }
        }
        return result;
    }

    //TODO: when in check, can not castle
    public List<Move> getLegalMoves(){
        //first, find out what's being pinned
        Map<Piece, Set<Pos>> pins = new HashMap<>();
        for (var o : diagonalOffset){
            RCP.pinningPath(true, pins, new HashSet<>(), this, Optional.empty(), getCurrentKing(), o);
        }
        for (var o : straightOffset){
            RCP.pinningPath(false, pins, new HashSet<>(), this, Optional.empty(), getCurrentKing(), o);
        }

        List<Move> moves = new ArrayList<>();
        for (var entry : this.board){
            Piece p = entry.getValue();
            Pos pos = entry.getKey();
            if (p.side() == this.side){
                p.addLegalMoves(moves, pins.get(p), pos, this);
            }
        }
        return moves;
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

        //PROMOTION
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
                // put the white king rook back
                if(move.end.x() == 7 && move.end.y == 1){
                    Piece rook = this.board.data.remove(new Pos(6, 1));
                    this.board.data.put(new Pos(8, 1), rook);
                }
                // put the white queen rook back
                else if(move.end.x() == 3 && move.end.y == 1){
                    Piece rook = this.board.data.remove(new Pos(4, 1));
                    this.board.data.put(new Pos(1, 1), rook);
                }
                // put the black king rook back
                else if(move.end.x() == 7 && move.end.y == 8){
                    Piece rook = this.board.data.remove(new Pos(6, 8));
                    this.board.data.put(new Pos(8, 8), rook);
                }
                // put the black queen rook back
                else if(move.end.x() == 3 && move.end.y == 8){
                    Piece rook = this.board.data.remove(new Pos(4, 8));
                    this.board.data.put(new Pos(1, 8), rook);
                }
            }
        }
    }

    //                          HELPER FUNCTIONS
}
