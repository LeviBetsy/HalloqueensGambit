package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.Optional;
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
        return 100000*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♚ ";
        else
            return " ♔ ";
    }

    @Override
    public ArrayList<Move> allLegalMove(Pos pos, Board board){
        ArrayList<Pos> legalPos = new ArrayList<>();
        Pos[] nextPos = {
                new Pos(pos.x() + 1, pos.y() + 1),
                new Pos(pos.x() + 1, pos.y()),
                new Pos(pos.x() + 1, pos.y() - 1),
                new Pos(pos.x(), pos.y() + 1),
                new Pos(pos.x(), pos.y() - 1),
                new Pos(pos.x() - 1, pos.y() + 1),
                new Pos(pos.x() - 1, pos.y()),
                new Pos(pos.x() - 1, pos.y() - 1)
        };
        for (Pos end : nextPos) {
            if (Game.inBound(end) && board.notAlly(end, this.side))
                legalPos.add(end);
        }

        //convert all legal positions into legalMoves
        ArrayList<Move> result = legalPos.stream()
                .map(end -> new Move(pos, end))
                .collect(Collectors.toCollection(ArrayList::new));

        //CASTLING
        if (!this.hasMoved) {
            if (this.side == Side.WHITE && pos.equals(new Pos(5, 1))){
                if (rookCanCastle(new Pos(1, 1), pos, board))
                    result.add(new Move(pos, new Pos(3, 1)));
                if (rookCanCastle(new Pos(8,1), pos, board))
                    result.add(new Move(pos, new Pos(7, 1)));
            } else if (this.side == Side.BLACK && pos.equals(new Pos(5, 8))){
                if (rookCanCastle(new Pos(1, 8), pos, board))
                    result.add(new Move(pos, new Pos(3, 8)));
                if (rookCanCastle(new Pos(8,8), pos, board))
                    result.add(new Move(pos, new Pos(7, 8)));
            }
        }
        return result;
    }
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