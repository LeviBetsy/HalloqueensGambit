package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Bishop implements Piece{
    private Side side;
    private Pos pos;
    public Bishop(Side side, Pos pos){
        this.side = side;
        this.pos = pos;
    }

    @Override
    public Pos pos() {return this.pos;}
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
    public ArrayList<Game.Move> allLegalMove(Board board){
        ArrayList<Pos> legalPos = new ArrayList<>();
        Game.OffSet[] offsets = {
                new Game.OffSet(1, 1),
                new Game.OffSet(1, -1),
                new Game.OffSet(-1, 1),
                new Game.OffSet(-1, -1)
        };

        //GENERATING ALL LEGAL POSITIONS
        for (Game.OffSet offset : offsets) {
            legalPos = RCP.RecurCheckPath(legalPos, board, this.side, this.pos, offset);
        }

        //DERIVING ALL LEGAL MOVES FROM THOSE POSITION
        ArrayList<Game.Move> legalMoves = legalPos.stream()
                .map(end -> new Game.Move(new Bishop(this.side, end), this.pos, end)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
        return legalMoves;
    }
}
