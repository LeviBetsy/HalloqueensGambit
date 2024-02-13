package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;
import halloqueensgambit.java.Move;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Knight implements Piece{
    private final Side side;
    public Knight(Side side){
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
            return " ♞ ";
        else
            return " ♘ ";
    }

    @Override
    public ArrayList<Move> allLegalMove(Pos pos, Board board){
        ArrayList<Pos> legalPos = new ArrayList<>();

        Pos[] nextPos = {
            new Pos(pos.x() + 1, pos.y() + 2),
            new Pos(pos.x() + 1, pos.y() - 2),
            new Pos(pos.x() - 1, pos.y() + 2),
            new Pos(pos.x() - 1, pos.y() - 2),
            new Pos(pos.x() + 2, pos.y() + 1),
            new Pos(pos.x() + 2, pos.y() - 1),
            new Pos(pos.x() - 2, pos.y() + 1),
            new Pos(pos.x() - 2, pos.y() - 1)
        };

        for (Pos p : nextPos) {
            if (Game.inBound(p) && board.notAlly(p, this.side)){
                legalPos.add(p);
            }
        }

        return legalPos.stream()
                .map(end -> new Move(pos, end)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
