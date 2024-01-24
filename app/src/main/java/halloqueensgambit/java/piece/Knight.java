package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Knight implements Piece{
    private Side side;
    private Game.Pos pos;
    public Knight(Side side, Game.Pos pos){
        this.side = side;
        this.pos = pos;
    }
    
    @Override
    public Game.Pos pos() { return this.pos;}

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
    public ArrayList<Game.Move> allLegalMove(Board board){
        ArrayList<Game.Pos> legalPos = new ArrayList<>();

        Game.Pos[] nextPos = {
            new Game.Pos(this.pos.x() + 1, this.pos.y() + 2),
            new Game.Pos(this.pos.x() + 1, this.pos.y() - 2),
            new Game.Pos(this.pos.x() - 1, this.pos.y() + 2),
            new Game.Pos(this.pos.x() - 1, this.pos.y() - 2),
            new Game.Pos(this.pos.x() + 2, this.pos.y() + 1),
            new Game.Pos(this.pos.x() + 2, this.pos.y() - 1),
            new Game.Pos(this.pos.x() - 2, this.pos.y() + 1),
            new Game.Pos(this.pos.x() - 2, this.pos.y() - 1)
        };

        for (Game.Pos p : nextPos) {
            if (Game.inBound(p) && board.notAlly(p, this.side)){
                legalPos.add(p);
            }
        }

        ArrayList<Game.Move> result = legalPos.stream()
                .map(end -> new Game.Move(new Knight(this.side, end), this.pos, end)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }
}
