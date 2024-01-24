package halloqueensgambit.java.piece;
import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;
import java.util.stream.Collectors;

import java.util.ArrayList;

public class Rook implements Piece {
    private Side side;
    private Game.Pos pos;
    public boolean hasMoved;
    public Rook(Side side, Game.Pos pos, boolean hasMoved){
        this.side = side;
        this.pos = pos;
        this.hasMoved = hasMoved;
    }

    @Override
    public Game.Pos pos() {return this.pos;}

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
    public ArrayList<Game.Move> allLegalMove(Board board){
        ArrayList<Game.Pos> legalPos = new ArrayList<>();
        Game.OffSet[] offsets = {
                new Game.OffSet(0, 1),
                new Game.OffSet(0, -1),
                new Game.OffSet(1, 0),
                new Game.OffSet(-1, 0)
        };

        for (Game.OffSet offset : offsets) {
            legalPos = RCP.RecurCheckPath(legalPos, board, this.side, this.pos, offset);
        }

        ArrayList<Game.Move> result = legalPos.stream()
                .map(end -> new Game.Move(new Rook(this.side, end, true), this.pos, end)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }

}
