package halloqueensgambit.java.piece;
import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;
import java.util.stream.Collectors;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Move;

import java.util.ArrayList;

public class Rook implements Piece {
    private final Side side;
    public boolean hasMoved;
    public Rook(Side side, boolean hasMoved){
        this.side = side;
        this.hasMoved = hasMoved;
    }
    
    @Override
    public Side side() {
        return this.side;
    }

    @Override
    public int value() {
        return 5*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♜ ";
        else
            return " ♖ ";
    }

    @Override
    public ArrayList<Move> allLegalMove(Pos pos, Board board){
        ArrayList<Pos> legalPos = new ArrayList<>();
        Game.OffSet[] offsets = {
                new Game.OffSet(0, 1),
                new Game.OffSet(0, -1),
                new Game.OffSet(1, 0),
                new Game.OffSet(-1, 0)
        };

        for (Game.OffSet offset : offsets) {
            legalPos = RCP.RecurCheckPath(legalPos, board, this.side, pos, offset);
        }

        ArrayList<Move> result = legalPos.stream()
                .map(end -> new Move(pos, end)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }

}
