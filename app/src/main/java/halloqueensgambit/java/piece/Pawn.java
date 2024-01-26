package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import static halloqueensgambit.java.Side.*;

public class Pawn implements Piece{
    private Side side;
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
    public ArrayList<Game.Move> allLegalMove(Pos pos, Board board){
        //TODO: add promotion here
        ArrayList<Game.Move> result = new ArrayList<>();
        if (this.side == WHITE){ //WHITE PAWN
            //PUSHING ONCE
            Pos pushOnce = new Pos(pos.x(), pos.y()+ 1);
            if (board.lookupBoard(pushOnce).isEmpty() ){
                result.add(new Game.Move(pos, pushOnce));
                //PUSHING TWICE
                Pos pushTwice = new Pos(pos.x(), pos.y()+ 2);
                if (board.lookupBoard(pushTwice).isEmpty()){
                    result.add(new Game.Move(pos, pushTwice));
                }
            }

            //CAPTURES
            Pos[] nextPos = {
                    new Pos(pos.x() + 1, pos.y() + 1),
                    new Pos(pos.x() - 1, pos.y() + 1)
            };
            for (Pos p : nextPos){
                var target = board.lookupBoard(p);
                if (Game.inBound(p) && target.isPresent() && target.get().side() != this.side){
                    result.add(new Game.Move(pos, p));
                }
            }
        } else { //BLACK PAWN
            //PUSHING ONCE
            Pos pushOnce = new Pos(pos.x(), pos.y()- 1);
            if (board.lookupBoard(pushOnce).isEmpty() ){
                result.add(new Game.Move(pos, pushOnce));
                //PUSHING TWICE
                Pos pushTwice = new Pos(pos.x(), pos.y()- 2);
                if (board.lookupBoard(pushTwice).isEmpty()){
                    result.add(new Game.Move(pos, pushTwice));
                }
            }

            //CAPTURES
            Pos[] nextPos = {
                    new Pos(pos.x() + 1, pos.y() - 1),
                    new Pos(pos.x() - 1, pos.y() - 1)
            };
            for (Pos p : nextPos){
                var target = board.lookupBoard(p);
                if (Game.inBound(p) && target.isPresent() && target.get().side() != this.side){
                    result.add(new Game.Move(pos, p));
                }
            }

        }
        return result;
    }
}
