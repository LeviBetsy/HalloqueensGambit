package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;

import java.util.*;
import java.util.stream.Collectors;

public class Game{
    private Side side;
    private Board board;

    //TODO: update king positions every time you make Move and unmakeMove
    private Pos wKing;
    private Pos bKing;
    private int evaluation;
    private long zobristHash;
    private long[] zobristNumbers;
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

    public Pos getEnemyKing() {
        return (this.side == Side.WHITE) ? bKing : wKing;
    }

    public Game(){
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Game(Side side, Board board){
        this.side = side;
        this.board = board;
        this.evaluation = board.evaluate();
        this.wKing = board.findKing(Side.WHITE);
        this.bKing = board.findKing(Side.BLACK);
        this.zobristNumbers = new long[769];
        for(int i = 0; i < 769; i++){
            zobristNumbers[i] = (long) (Math.random() * Math.pow(2, 64) - 1);
        }
        generateZobristHash();

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
        this.zobristNumbers = new long[769];

        for(int i = 0; i < 769; i++){
            zobristNumbers[i] = (long) (Math.random() * Math.pow(2, 64) - 1);
        }
        generateZobristHash();
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
        return (dangerousSquares().contains(getCurrentKing()));
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
        //IN CHECK
        if (dangerousSquares().contains(getCurrentKing())){
            Optional<Set<Pos>> checkBreakers = getViableCheckBreakers();
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
            King king = (King) this.board.data.get(getCurrentKing());
            king.addLegalMovesNoCastle(moves, null, getCurrentKing(), this);
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

    private int getZobristIndex(Pos pos, Piece piece){
        int index = (pos.x-1) + (pos.y-1) * 8;
        
        if(piece instanceof Pawn){
            index = 0;
        }else if(piece instanceof Knight){
            index = 1;
        }else if(piece instanceof Bishop){
            index = 2;
        }else if(piece instanceof Rook){
            index = 3;
        }else if(piece instanceof Queen){
            index = 4;
        }else{
            index = 5;
        }
        if(piece.side() == Side.BLACK){
            index += 6;
        }
        return index;
    }

    public long getZobristHash(){
        return this.zobristHash;
    }

    public long generateZobristHash(){
        // 781 (12*64 + 1 + 4 + 8)
        // TODO: en passant and castling rights for zobrist numbers. 
        // for now they are left out. this will cause bugs in certain positions involving castling/en passant.
        long hash = 0;
        int sideIsBlack = 768;

        for(Map.Entry<Pos, Piece> entry: this.board){
            Pos pos = entry.getKey();
            Piece piece = entry.getValue();

            //pawn, knight, bishop, rook, queen, king * 2
            int index = getZobristIndex(pos, piece);

            hash = hash ^ zobristNumbers[index];
        }

        if(this.side == Side.BLACK){
            hash = hash ^ zobristNumbers[sideIsBlack];
        }

        this.zobristHash = hash;
        return hash;
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
        zobristHash = zobristHash ^ this.zobristNumbers[768];

        Piece movingPiece = this.board.data.remove(move.start);
        zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(move.start, movingPiece)];

        if (movingPiece instanceof King) {
            if (movingPiece.side() == Side.WHITE)
                wKing = move.end;
            else
                bKing = move.end;
        } else if (move.end == wKing)
            wKing = null;
        else if (move.end == bKing)
            bKing = null;

        if (movingPiece instanceof King){
            ((King) movingPiece).hasMoved = true;
        } else if (movingPiece instanceof Rook){
            ((Rook) movingPiece).hasMoved = true;
        }

        //PROMOTION
        if (movingPiece instanceof Pawn) {
            if (move.end.y() == 1 || move.end.y() == 8){
                Queen newQueen = new Queen(movingPiece.side());
                this.board.data.put(move.end, newQueen);
                zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(move.end, newQueen)];

                //handling evaluation
                this.evaluation -= movingPiece.value();
                this.evaluation += newQueen.value();
            }
        } 

        //CASTLING
        else if (movingPiece instanceof King) {
            int castleY = (movingPiece.side() == Side.WHITE) ? 1 : 8;
            //CASTLING QUEEN SIDE
            if (move.start.equals(new Pos(5,castleY))
                    && move.end.equals(new Pos(3,castleY))){
                //MOVING THE ROOK
                Piece movingRook = this.board.data.remove(new Pos(1,castleY));
                zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(new Pos(1,castleY), movingRook)];
                this.board.data.put(new Pos(4,castleY), new Rook(movingPiece.side(), true));
                zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(new Pos(4,castleY), movingRook)];
            //CASTLING KING SIDE
            } else if (move.start.equals(new Pos(5, castleY))
                    && move.end.equals(new Pos(7, castleY))){
                //MOVING THE ROOK
                Piece movingRook = this.board.data.remove(new Pos(8,castleY));
                zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(new Pos(8,castleY), movingRook)];
                this.board.data.put(new Pos(6,castleY), new Rook(movingPiece.side(), true));
                zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(new Pos(6,castleY), movingRook)];
            }
        } 

        this.board.data.put(move.end, movingPiece);
        zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(move.end, movingPiece)];

        captured.ifPresent(piece -> this.evaluation -= piece.value());
        return captured;
    }

    public void unMakeMove(Move move, Optional<Piece> captured){
        this.side = Side.opponent(side);
        zobristHash = zobristHash ^ this.zobristNumbers[768];

        Piece movingPiece = this.board.data.remove(move.end);
        zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(move.end, movingPiece)];

        if (movingPiece instanceof King){
            if (movingPiece.side() == Side.WHITE)
                this.wKing = move.start;
            else
                this.bKing = move.start;
        }
        
        // handle captures
        if(captured.isPresent()) {
            this.board.data.put(move.end, captured.get());
            zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(move.end, captured.get())];
            this.evaluation += captured.get().value();
        }

        // put the moving piece back where it goes
        this.board.data.put(move.start, movingPiece);
        zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(move.start, movingPiece)];

        //PROMOTION 
        if(move.isPromotion){
            Pawn pawn = new Pawn(this.side);
            this.board.data.put(move.start, pawn);
            zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(move.start, pawn)];
            //handling evaluation
            this.evaluation += pawn.value();
            this.evaluation -= movingPiece.value();
        }

        // check castles
        if(movingPiece instanceof King){
            // if the king moves more than one space, it must have castled
            if(move.getDistance() > 1){
                ((King) movingPiece).hasMoved = false;
                int castleY = (movingPiece.side() == Side.WHITE) ? 1 : 8;
                // put the king rook back
                if(move.end.x() == 7){
                    Rook rook = (Rook) this.board.data.remove(new Pos(6, castleY));
                    zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(new Pos(6, castleY), rook)];
                    rook.hasMoved = false;
                    this.board.data.put(new Pos(8, castleY), rook);
                    zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(new Pos(8, castleY), rook)];

                }
                // put the queen rook back 
                else if(move.end.x() == 3){
                    Rook rook = (Rook) this.board.data.remove(new Pos(4, castleY));
                    zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(new Pos(4, castleY), rook)];
                    rook.hasMoved = false;
                    this.board.data.put(new Pos(1, castleY), rook);
                    zobristHash = zobristHash ^ this.zobristNumbers[getZobristIndex(new Pos(1, castleY), rook)];
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
                if (controlledSquares.contains(getCurrentKing())){
                    //if there is another check found previously then this is a double check
                    if (!result.isEmpty()){
                        return  Optional.empty();
                    } else {
                        if (p instanceof Rook || p instanceof Bishop || p instanceof Queen){
                            RCP.addCheckedPath(result, pos, this.board, findOffSet(pos, getCurrentKing()));
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
