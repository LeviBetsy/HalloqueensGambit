package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;
import halloqueensgambit.java.Move;

import java.util.List;
import java.util.Set;

public class Bishop implements Piece{
    private final Side side;
    public Bishop(Side side){
        this.side = side;
    }
    @Override
    public Side side() {
        return this.side;
    }

    @Override
    public int value() {
        return Game.bishopValue*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♝ ";
        else
            return " ♗ ";
    }

    public String toLetter(){
        if (side == Side.WHITE)
            return "B";
        else
            return "b";
    }

    @Override
    public void addLegalMoves(List<Move> moves, Set<Pos> pinnedPath, Pos pos, Game game){
        for (Game.OffSet o : Game.diagonalOffset)
            RCP.recurAddMove(moves, pinnedPath, game.board(), this.side, pos, pos, o);
    }

    @Override
    public void addControllingSquares(Set<Pos> squares, Pos pos, Board board){
        Game.OffSet[] offsets = {
                new Game.OffSet(1, 1),
                new Game.OffSet(1, -1),
                new Game.OffSet(-1, 1),
                new Game.OffSet(-1, -1)
        };

        for (var o : offsets){
            RCP.addControlSquares(squares, this.side, pos, board, o);
        }
    }
}
