package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;

import java.util.*;
import java.util.stream.Collectors;

public class Game{
    private Side side;
    private Board board;
    private Pos wKingPos;
    private Pos bKingPos;
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

    public Pos getCurrentKingPos(){
        return (this.side == Side.WHITE) ? wKingPos : bKingPos;
    }
    
    public Game(Side side, Board board){
        this.side = side;
        this.board = board;
        this.evaluation = board.evaluate();
        this.wKingPos = board.findKing(Side.WHITE);
        this.bKingPos = board.findKing(Side.BLACK);
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
        this.wKingPos = board.findKing(Side.WHITE);
        this.bKingPos = board.findKing(Side.BLACK);
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
            return letters[x-1] + Integer.toString(y);
        }
    }

    public static record OffSet(int dx, int dy){};

    public static final int kingValue = 1000000;
    public static final int pawnValue = 100;
    public static final int queenValue = 900;
    public static final int knightValue = 300;
    public static final int rookValue = 500;
    public static final int bishopValue = 300;
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

    public static boolean inBound(int x, int y){
        return (x >= 1 && x <= 8 && y >= 1 && y <= 8);
    }

    //return a set of all the squares being controlled by the enemy
    public Set<Pos> dangerousSquares(){
        Set<Pos> result = new HashSet<>();
        for (var entry : this.board){
            Piece p = entry.getValue();
            if (p.side() == Side.opponent(this.side)){
                p.addControllingSquares(result, entry.getKey(), this.board);
            }
        }
        return result;
    }

    public boolean inCheck(){
        return (dangerousSquares().contains(getCurrentKingPos()));
    }

    public List<Move> getLegalMoves(){
        //first, find out what's being pinned
        Map<Piece, Set<Pos>> pins = new HashMap<>();
        for (var o : diagonalOffset){
            RCP.pinningPath(true, pins, new HashSet<>(), this, Optional.empty(), getCurrentKingPos(), o);
        }
        for (var o : straightOffset){
            RCP.pinningPath(false, pins, new HashSet<>(), this, Optional.empty(), getCurrentKingPos(), o);
        }

        List<Move> moves = new ArrayList<>();
        //IN CHECK
        if (dangerousSquares().contains(getCurrentKingPos())){
            Optional<Set<Pos>> checkBreakers = getViableCheckBreakers();
            //if there is a change to break check with other pieces
            if (checkBreakers.isPresent()) {
                for (var entry : this.board) {
                    Piece p = entry.getValue();
                    Pos pos = entry.getKey();
                    if (p.side() == this.side && !(p instanceof King)) {
                        p.addLegalMoves(moves, pins.get(p), pos, this);
                    }
                }
                //only keeps the move that breaks the check
                moves = moves.stream().filter(m -> checkBreakers.get().contains(m.end)).collect(Collectors.toList());
            }
            King king = (King) this.board.data.get(getCurrentKingPos());
            king.addLegalMovesNoCastle(moves, getCurrentKingPos(), this);
        } else {
            for (var entry : this.board) {
                Piece p = entry.getValue();
                Pos pos = entry.getKey();
                if (p.side() == this.side) {
                    p.addLegalMoves(moves, pins.get(p), pos, this);
                }
            }
        }
        return moves;
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
        Piece movingPiece = this.board.data.remove(move.start);

        this.side = Side.opponent(side);
        //moving king
        if (movingPiece instanceof King) {
            if (movingPiece.side() == Side.WHITE)
                wKingPos = move.end;
            else
                bKingPos = move.end;
            ((King) movingPiece).hasMoved = true;
        }

        if (movingPiece instanceof Rook){
            ((Rook) movingPiece).hasMoved = true;
        }

        //PROMOTION
        if (move.isPromotion) {
            Queen newQueen = new Queen(movingPiece.side());
            this.board.data.put(move.end, newQueen);
            //handling evaluation
            this.evaluation -= movingPiece.value();
            this.evaluation += newQueen.value();

        }
        //CASTLING
        else if (movingPiece instanceof King) {
            int castleY = (movingPiece.side() == Side.WHITE) ? 1 : 8;
            //CASTLING QUEEN SIDE
            if (move.start.equals(new Pos(5,castleY))
                    && move.end.equals(new Pos(3,castleY))){
                //MOVING THE ROOK
                this.board.data.remove(new Pos(1,castleY));
                this.board.data.put(new Pos(4,castleY), new Rook(movingPiece.side(), true));
                this.board.data.put(move.end, movingPiece);
            //CASTLING KING SIDE
            } else if (move.start.equals(new Pos(5, castleY))
                    && move.end.equals(new Pos(7, castleY))){
                //MOVING THE ROOK
                this.board.data.remove(new Pos(8,castleY));
                this.board.data.put(new Pos(6,castleY), new Rook(movingPiece.side(), true));
                this.board.data.put(move.end, movingPiece);
            } else {
                this.board.data.put(move.end, movingPiece);
            }
        } else {
            this.board.data.put(move.end, movingPiece);
        }

        //remove the captured piece value from the evaluation
        captured.ifPresent(piece -> this.evaluation -= piece.value());
        return captured;
    }

    public void unMakeMove(Move move, Optional<Piece> captured){
        this.side = Side.opponent(side);
        Piece movingPiece = this.board.data.remove(move.end);

        if (movingPiece instanceof King){
            if (movingPiece.side() == Side.WHITE)
                this.wKingPos = move.start;
            else
                this.bKingPos = move.start;
        }
        
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
        //CHECK CASTLES
        else if(movingPiece instanceof King){
            // if the king moves more than one space, it must have castled
            if(move.getDistance() > 1){
                ((King) movingPiece).hasMoved = false;
                int castleY = (movingPiece.side() == Side.WHITE) ? 1 : 8;
                // put the king rook back
                if(move.end.x() == 7){
                    Rook rook = (Rook) this.board.data.remove(new Pos(6, castleY));
                    rook.hasMoved = false;
                    this.board.data.put(new Pos(8, castleY), rook);
                }
                // put the queen rook back
                else if(move.end.x() == 3){
                    Rook rook = (Rook) this.board.data.remove(new Pos(4, castleY));
                    rook.hasMoved = false;
                    this.board.data.put(new Pos(1, castleY), rook);
                }
            }
        }
    }

    //                                  HELPER FUNCTION

    //Return a list of position a non-king piece must take in order to break the check
    //A valid position might be a blocking or taking of the checking enemy piece
    //It will return EMPTY if there is a DOUBLE CHECK thus you can not break the check only move king
    private Optional<Set<Pos>> getViableCheckBreakers(){
        Set<Pos> result = new HashSet<>();
        for (var entry : this.board){
            Piece p = entry.getValue();
            Pos pos = entry.getKey();
            //loops through all enemy pieces to find the one checking our king
            if (p.side() == Side.opponent(this.side)){
                Set<Pos> controlledSquares = new HashSet<>();
                p.addControllingSquares(controlledSquares, pos, this.board);
                if (controlledSquares.contains(getCurrentKingPos())){
                    //if there is another check found previously then this is a double check
                    if (!result.isEmpty()){
                        return Optional.empty();
                    } else {
                        if (p instanceof Rook || p instanceof Bishop || p instanceof Queen){
                            RCP.addCheckedPath(result, pos, this.board, findOffSet(pos, getCurrentKingPos()));
                        }
                        //taking of the checking piece is also a viable check breaker
                        result.add(pos);
                    }
                }
            }
        }
        return Optional.of(result);
    }

    private OffSet findOffSet(Pos start, Pos end){
        int dx = (end.x - start.x == 0) ? 0 : (end.x - start.x)/Math.abs(end.x- start.x);
        int dy = (end.y - start.y == 0) ? 0 : (end.y - start.y)/Math.abs(end.y- start.y);
        return new OffSet(dx, dy);
    }
}
