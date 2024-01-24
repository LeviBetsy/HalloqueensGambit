package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class King implements Piece{

    private Side side;
    private Game.Pos pos;
    private boolean hasMoved;
    public King(Side side, Game.Pos pos, boolean hasMoved){
        this.side = side;
        this.pos = pos;
        this.hasMoved = hasMoved;
    }

    @Override
    public Game.Pos pos() {return  this.pos;}
    @Override
    public Side side() {
        return this.side;
    }

    @Override
    public int value() {
        return 100000*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♚ ";
        else
            return " ♔ ";
    }

    @Override
    public ArrayList<Game.Move> allLegalMove(Board board){
        ArrayList<Game.Pos> legalPos = new ArrayList<>();
        Game.Pos[] nextPos = {
                new Game.Pos(this.pos.x() + 1, this.pos.y() + 1),
                new Game.Pos(this.pos.x() + 1, this.pos.y()),
                new Game.Pos(this.pos.x() + 1, this.pos.y() - 1),
                new Game.Pos(this.pos.x(), this.pos.y() + 1),
                new Game.Pos(this.pos.x(), this.pos.y() - 1),
                new Game.Pos(this.pos.x() - 1, this.pos.y() + 1),
                new Game.Pos(this.pos.x() - 1, this.pos.y()),
                new Game.Pos(this.pos.x() - 1, this.pos.y() - 1)
        };

        for (Game.Pos p : nextPos) {
            if (Game.inBound(p) && board.notAlly(p, this.side)){
                legalPos.add(p);
            }
        }

        ArrayList<Game.Move> result = legalPos.stream()
                .map(end -> new Game.Move(new King(this.side, end, true), this.pos, end)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }
}
