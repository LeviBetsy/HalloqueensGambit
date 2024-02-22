package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.*;
import java.util.stream.Collectors;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Move;


public class King implements Piece{
    private final Side side;
    public boolean hasMoved;
    public King(Side side, boolean hasMoved){
        this.side = side;
        this.hasMoved = hasMoved;
    }

    @Override
    public Side side() {
        return this.side;
    }

    @Override
    public int value() {
        return Game.kingValue*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♚ ";
        else
            return " ♔ ";
    }

    public String toLetter(){
        if (side == Side.WHITE)
            return "K";
        else
            return "k";
    }


    //we can't really be pinned as a king so pinnedPath will always be null
    @Override
    public void addLegalMoves(List<Move> moves, Set<Pos> pinnedPath, Pos pos, Game game){
        Board board = game.board();
        Set<Pos> dangerousSquares = game.dangerousSquares();
        for (Pos nextPos : kingSquares(pos)){
            //can only move to empty square or enemy square
            //the square you're going to must not be dangerous
            if (board.notAlly(nextPos, this.side) && !dangerousSquares.contains(nextPos)){
                moves.add(new Move(pos, nextPos));
            }
        }

        //CASTLING
        int y = (this.side == Side.WHITE) ? 1 : 8;
        if (!this.hasMoved && pos.equals(new Pos(5, y))) {
            Pos lCastlePos = new Pos(3, y);
            if (rookCanCastle(new Pos(1, y), pos, board) && !dangerousSquares.contains(lCastlePos))
                moves.add(new Move(pos, lCastlePos));
            Pos rCastlePos = new Pos(7, y);
            if (rookCanCastle(new Pos(8,y), pos, board) && !dangerousSquares.contains(rCastlePos))
                moves.add(new Move(pos, rCastlePos));
        }
    }

    public void addLegalMovesNoCastle(List<Move> moves, Pos pos, Game game) {
        Board board = game.board();
        Set<Pos> dangerousSquares = game.dangerousSquares();
        for (Pos nextPos : kingSquares(pos)) {
            //can only move to empty square or enemy square
            //the square you're going to must not be dangerous
            if (board.notAlly(nextPos, this.side) && !dangerousSquares.contains(nextPos)) {
                moves.add(new Move(pos, nextPos));
            }
        }
    }

    @Override
    public void addControllingSquares(Set<Pos> squares, Pos pos, Board board) {
        squares.addAll(kingSquares(pos));
    }

    private List<Pos> kingSquares(Pos pos){
        Pos[] nextPos = {
                new Pos(pos.x() + 1, pos.y() + 1),
                new Pos(pos.x() + 1, pos.y() ),
                new Pos(pos.x() + 1, pos.y() -1),
                new Pos(pos.x() , pos.y() + 1),
                new Pos(pos.x() , pos.y() - 1),
                new Pos(pos.x() - 1, pos.y() + 1),
                new Pos(pos.x() - 1, pos.y()),
                new Pos(pos.x() - 1, pos.y() - 1)
        };
        return Arrays.stream(nextPos).filter(Game::inBound).collect(Collectors.toList());
    }

    //                           HELPER FUNCTIONS
    //returns true if it is valid to castle with the rook and the rook exists
    private boolean rookCanCastle(Pos rookPos, Pos kingPos, Board board){
        Optional<Piece> rook = board.lookup(rookPos.x(), rookPos.y());
        return rook.isPresent() && rook.get() instanceof Rook
                && rook.get().side() == this.side && !((Rook) rook.get()).hasMoved
                && blankBetween(rookPos, kingPos, board);
    }

    //returns true if the spaces between a king and a rook is all blank
    private boolean blankBetween(Pos rookPos, Pos kingPos, Board board){
        int multiplier = 1;
        if (rookPos.x() < kingPos.x())
            multiplier = -1;
        //kingPos.x() + (dx * multiplier) == x coordinate of the spaces in between
        for (int dx = 1; (kingPos.x() + (dx * multiplier) != rookPos.x()); dx++) {
            if (board.lookup(kingPos.x() + (dx * multiplier), kingPos.y()).isPresent())
                return false;
        }
        return true;
    }
}