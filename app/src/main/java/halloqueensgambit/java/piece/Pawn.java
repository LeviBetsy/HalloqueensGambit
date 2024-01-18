package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import static halloqueensgambit.java.Side.WHITE;
import static halloqueensgambit.java.Side.BLACK;

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
    public ArrayList<Game.Move> allLegalMove(Game.Pos start, Board board){
        ArrayList<Game.Move> result = new ArrayList<>();
        if (this.side == WHITE){
            Game.Pos pushOnce = new Game.Pos(start.x(), start.y()+ 1);
            if (board.lookupBoard(pushOnce).isEmpty() ){
                result.add(new Game.Move(this, start, pushOnce));
                Game.Pos pushTwice = new Game.Pos(start.x(), start.y()+ 2);
                if (board.lookupBoard(pushTwice).isEmpty()){
                    result.add(new Game.Move(this, start, pushTwice));
                }
            }

            //CAPTURES
            Game.Pos[] nextPos = {
                    new Game.Pos(start.x() + 1, start.y() + 1),
                    new Game.Pos(start.x() - 1, start.y() + 1)
            };
            for (Game.Pos p : nextPos){
                var target = board.lookupBoard(p);
                if (Game.inBound(p) && target.isPresent() && target.get().side() != this.side){
                    result.add(new Game.Move(this, start, p));
                }
            }
        } else {
            Game.Pos pushOnce = new Game.Pos(start.x(), start.y()- 1);
            if (board.lookupBoard(pushOnce).isEmpty() ){
                result.add(new Game.Move(this, start, pushOnce));
                Game.Pos pushTwice = new Game.Pos(start.x(), start.y()- 2);
                if (board.lookupBoard(pushTwice).isEmpty()){
                    result.add(new Game.Move(this, start, pushTwice));
                }
            }

            //CAPTURES
            Game.Pos[] nextPos = {
                    new Game.Pos(start.x() + 1, start.y() - 1),
                    new Game.Pos(start.x() - 1, start.y() - 1)
            };
            for (Game.Pos p : nextPos){
                var target = board.lookupBoard(p);
                if (Game.inBound(p) && target.isPresent() && target.get().side() != this.side){
                    result.add(new Game.Move(this, start, p));
                }
            }
        }
        return result;
    }
}
