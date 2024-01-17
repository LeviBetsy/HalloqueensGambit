package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;

public class Queen implements Piece{
    private Side side;
    public Queen(Side side){
        this.side = side;
    }

    @Override
    public Side side() {
        return this.side;
    }

    @Override
    public int value() {
        return 9*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♛ ";
        else
            return " ♕ ";
    }

    @Override
    public ArrayList<Game.Move> allLegalMove(Game.Pos start, Board board){
        throw new UnsupportedOperationException();
    }
}
