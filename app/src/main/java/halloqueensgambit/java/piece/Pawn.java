package halloqueensgambit.java.piece;

import halloqueensgambit.java.*;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static halloqueensgambit.java.Side.*;

public class Pawn implements Piece{
    private final Side side;
    private int dir;
    public Pawn(Side side){
        this.side = side;
        this.dir = (side == WHITE) ? 1 : -1;
    }

    @Override
    public Side side() {
        return this.side;
    }

    @Override
    public int value() {
        return side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♟ ";
        else
            return " ♙ ";
    }

    @Override
    public void addLegalMoves(List<Move> moves, Set<Pos> pinnedPath, Pos pos, Game game){
        List<Pos> nextPositions = pawnMoves(pos, game.board());
        for (Pos nextPos : nextPositions){
            if (pinnedPath == null || pinnedPath.contains(nextPos)){
                //PROMOTION
                if (nextPos.y() == 1 || nextPos.y() == 8)
                    moves.add(new Move(pos, nextPos, true));
                else
                    moves.add(new Move(pos, nextPos));
            }
        }
    }

    @Override
    public void addControllingSquares(Set<Pos> squares, Pos pos, Board board) {
        Pos left = new Pos(pos.x() + 1, pos.y() + this.dir);
        if (Game.inBound(left))
            squares.add(left);
        Pos right = new Pos(pos.x() - 1, pos.y() + this.dir);
        if (Game.inBound(right))
            squares.add(right);
    }

    //                                      HELPER FUNCTIONS

    private List<Pos> pawnMoves(Pos pos, Board board){
        List<Pos> result = new ArrayList<>();
        //PUSHING ONCE
        Pos pushOnce = new Pos(pos.x(), pos.y() + this.dir);
        if (board.lookup(pushOnce).isEmpty() ){
            result.add(pushOnce);
            //PUSHING TWICE
            if ((this.side == WHITE && pos.y() == 2) || (this.side == BLACK && pos.y() == 7)) {
                Pos pushTwice = new Pos(pos.x(), pos.y() + 2 * this.dir);
                if (board.lookup(pushTwice).isEmpty()) {
                    result.add(pushTwice);
                }
            }
        }

        //CAPTURES
        Pos[] caps = {
                new Pos(pos.x() + 1, pos.y() + this.dir),
                new Pos(pos.x() - 1, pos.y() + this.dir)
        };

        for (Pos cap : caps){
            var target = board.lookup(cap.x(), cap.y());
            if (Game.inBound(cap) && target.isPresent() && target.get().side() != this.side){
                result.add(cap);
            }
        }

        return result;
    }

}
