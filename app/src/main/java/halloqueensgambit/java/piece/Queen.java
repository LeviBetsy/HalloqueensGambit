package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Queen implements Piece{
    private final Side side;
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
    public ArrayList<Game.Move> allLegalMove(Pos pos, Board board){
        ArrayList<Pos> legalPos = new ArrayList<>();
        Game.OffSet[] offsets = {
            new Game.OffSet(1, 1),
            new Game.OffSet(1, -1),
            new Game.OffSet(-1, 1),
            new Game.OffSet(-1, -1),
            new Game.OffSet(0, 1),
            new Game.OffSet(0, -1),
            new Game.OffSet(1, 0),
            new Game.OffSet(-1, 0)
        };

        for (Game.OffSet offset : offsets) {
            legalPos = RCP.RecurCheckPath(legalPos, board, this.side, pos, offset);
        }

        ArrayList<Game.Move> result = legalPos.stream()
                .map(end -> new Game.Move(pos, end)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }
}
