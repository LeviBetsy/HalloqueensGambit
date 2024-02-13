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
        ArrayList<Move> result = new ArrayList<>();
        if (this.side == WHITE){ //WHITE PAWN
            //PUSHING ONCE
            if (board.lookup(pos.x(), pos.y()+ 1).isEmpty() ){
                result.add(new Move(pos, new Pos(pos.x(), pos.y())));
                //PUSHING TWICE
                if (board.lookup(pos.x(), pos.y()+ 2).isEmpty()){
                    result.add(new Move(pos, new Pos(pos.x(), pos.y()+ 2)));
                }
            }

            //CAPTURES
            Pos[] nextPos = {
                    new Pos(pos.x() + 1, pos.y() + 1),
                    new Pos(pos.x() - 1, pos.y() + 1)
            };
            for (Pos p : nextPos){
                var target = board.lookup(p.x(), p.y());
                if (Game.inBound(p) && target.isPresent() && target.get().side() != this.side){
                    result.add(new Move(pos, p));
                }
            }
        } else { //BLACK PAWN
            //PUSHING ONCE
            if (board.lookup(pos.x(), pos.y()- 1).isEmpty() ){
                result.add(new Move(pos, new Pos(pos.x(), pos.y()- 1)));
                //PUSHING TWICE
                if (board.lookup(pos.x(),pos.y()- 2).isEmpty()){
                    result.add(new Move(pos, new Pos(pos.x(),pos.y()- 2)));
                }
            }

            //CAPTURES
            Pos[] nextPos = {
                    new Pos(pos.x() + 1, pos.y() - 1),
                    new Pos(pos.x() - 1, pos.y() - 1)
            };
            for (Pos p : nextPos){
                var target = board.lookup(p.x(), p.y());
                if (Game.inBound(p) && target.isPresent() && target.get().side() != this.side){
                    result.add(new Move(pos, p));
                }
            }
        }
        for(int i = 0; i < result.size(); ++i){
            if(result.get(i).end.y() == 1 || result.get(i).end.y() == 8){
                result.get(i).isPromotion = true;
            }
        }
        return result;
    }
}
