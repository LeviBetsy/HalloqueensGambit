package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Knight implements Piece{
    private Side side;
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
    public ArrayList<Game.Move> allLegalMove(Game.Pos start, Board board){
        ArrayList<Game.Pos> legalPos = new ArrayList<>();

        Game.Pos[] nextPos = {
            new Game.Pos(start.x() + 1, start.y() + 2),
            new Game.Pos(start.x() + 1, start.y() - 2),
            new Game.Pos(start.x() - 1, start.y() + 2),
            new Game.Pos(start.x() - 1, start.y() + 2),
            new Game.Pos(start.x() + 2, start.y() + 1),
            new Game.Pos(start.x() + 2, start.y() - 1),
            new Game.Pos(start.x() - 2, start.y() + 1),
            new Game.Pos(start.x() - 2, start.y() - 1)
        };

        for (Game.Pos p : nextPos) {
            if (Game.inBound(p) && board.notAlly(p, this.side)){
                legalPos.add(p);
            }
        }

        ArrayList<Game.Move> result = legalPos.stream()
                .map(pos -> new Game.Move(this, start, pos)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }
}
