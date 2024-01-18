package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Bishop implements Piece{
    private Side side;
    public Bishop(Side side){
        this.side = side;
    }

    @Override
    public Side side() {
        return this.side;
    }

    @Override
    public int value() {
        return 3*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♝ ";
        else
            return " ♗ ";
    }

    @Override
    public ArrayList<Game.Move> allLegalMove(Game.Pos start, Board board){
        ArrayList<Game.Pos> legalPos = new ArrayList<>();
        Game.OffSet[] offsets = {
                new Game.OffSet(1, 1),
                new Game.OffSet(1, -1),
                new Game.OffSet(-1, 1),
                new Game.OffSet(-1, -1)
        };

        for (Game.OffSet offset : offsets) {
            legalPos = RCP.RecurCheckPath(legalPos, board, this.side, start, offset);
        }

        ArrayList<Game.Move> result = legalPos.stream()
                .map(pos -> new Game.Move(this, start, pos)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }
}
