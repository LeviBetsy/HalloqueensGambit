package halloqueensgambit.java.piece;

import halloqueensgambit.java.*;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import static halloqueensgambit.java.Side.*;

public class Pawn implements Piece{
    private final Side side;
    public Pawn(Side side){
        this.side = side;
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
    public ArrayList<Move> allLegalMove(Pos pos, Board board){
        if (this.side == WHITE){
            return pawnMoves(pos, board, 1);
        } else {
            return pawnMoves(pos, board, -1);
        }
    }

    public ArrayList<Move> pawnMoves(Pos pos, Board board, int dir){
        ArrayList<Move> result = new ArrayList<>();
        //PUSHING ONCE
        Pos pushOnce = new Pos(pos.x(), pos.y() + dir);
        if (board.lookup(pushOnce).isEmpty() ){
            //PROMOTION
            if ((pos.y() + dir == 8) || (pos.y() + dir == 1))
                result.add(new Move(pos, pushOnce, true));
            else
                result.add(new Move(pos, pushOnce));
            //PUSHING TWICE
            if ((this.side == WHITE && pos.y() == 2) || (this.side == BLACK && pos.y() == 7)) {
                Pos pushTwice = new Pos(pos.x(), pos.y() + 2 * dir);
                if (board.lookup(pushTwice).isEmpty()) {
                    result.add(new Move(pos, pushTwice));
                }
            }
        }

        //CAPTURES
        Pos[] caps = {
                new Pos(pos.x() + 1, pos.y() + dir),
                new Pos(pos.x() - 1, pos.y() + dir)
        };

        for (Pos cap : caps){
            var target = board.lookup(cap.x(), cap.y());
            if (Game.inBound(cap) && target.isPresent() && target.get().side() != this.side){
                if ((pos.y() + dir == 8) || (pos.y() + dir == 1))
                    result.add(new Move(pos, cap, true));
                else
                    result.add(new Move(pos, cap));
            }
        }

        return result;
    }

}
