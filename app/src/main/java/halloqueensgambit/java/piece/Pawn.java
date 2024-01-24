package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import static halloqueensgambit.java.Side.WHITE;
import static halloqueensgambit.java.Side.BLACK;

public class Pawn implements Piece{
    private Side side;
    private Pos pos;
    public Pawn(Side side, Pos pos){
        this.side = side;
        this.pos = pos;
    }

    @Override
    public Pos pos(){ return this.pos;}
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
    public ArrayList<Game.Move> allLegalMove(Board board){
        //TODO: add promotion here
        ArrayList<Game.Move> result = new ArrayList<>();
        if (this.side == WHITE){ //WHITE PAWN
            //PUSHING ONCE
            Pos pushOnce = new Pos(this.pos.x(), this.pos.y()+ 1);
            if (board.lookupBoard(pushOnce).isEmpty() ){
                result.add(new Game.Move(new Pawn(WHITE, pushOnce), this.pos, pushOnce));
                //PUSHING TWICE
                Pos pushTwice = new Pos(this.pos.x(), this.pos.y()+ 2);
                if (board.lookupBoard(pushTwice).isEmpty()){
                    result.add(new Game.Move(new Pawn(WHITE, pushTwice), this.pos, pushTwice));
                }
            }

            //CAPTURES
            Pos[] nextPos = {
                    new Pos(this.pos.x() + 1, this.pos.y() + 1),
                    new Pos(this.pos.x() - 1, this.pos.y() + 1)
            };
            for (Pos p : nextPos){
                var target = board.lookupBoard(p);
                if (Game.inBound(p) && target.isPresent() && target.get().side() != this.side){
                    result.add(new Game.Move(new Pawn(WHITE, p), this.pos, p));
                }
            }
        } else { //BLACK PAWN
            //PUSHING ONCE
            Pos pushOnce = new Pos(this.pos.x(), this.pos.y()- 1);
            if (board.lookupBoard(pushOnce).isEmpty() ){
                result.add(new Game.Move(new Pawn(BLACK, pushOnce), this.pos, pushOnce));
                //PUSHING TWICE
                Pos pushTwice = new Pos(this.pos.x(), this.pos.y()- 2);
                if (board.lookupBoard(pushTwice).isEmpty()){
                    result.add(new Game.Move(new Pawn(BLACK, pushTwice), this.pos, pushTwice));
                }
            }

            //CAPTURES
            Pos[] nextPos = {
                    new Pos(this.pos.x() + 1, this.pos.y() - 1),
                    new Pos(this.pos.x() - 1, this.pos.y() - 1)
            };
            for (Pos p : nextPos){
                var target = board.lookupBoard(p);
                if (Game.inBound(p) && target.isPresent() && target.get().side() != this.side){
                    result.add(new Game.Move(new Pawn(BLACK, p), this.pos, p));
                }
            }

        }
        return result;
    }
}
