package halloqueensgambit.java.piece;
import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.List;
import java.util.Set;
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
        return Game.rookValue*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♜ ";
        else
            return " ♖ ";
    }

    @Override
    public void addLegalMoves(List<Move> moves, Set<Pos> pinnedPath, Pos pos, Game game) {
        for (Game.OffSet o : Game.straightOffset)
            RCP.recurAddMove(moves, pinnedPath, game.board(), this.side, pos, pos, o);
    }

    @Override
    public void addControllingSquares(Set<Pos> squares, Pos pos, Board board){
        Game.OffSet[] offsets = {
                new Game.OffSet(0, 1),
                new Game.OffSet(0, -1),
                new Game.OffSet(1, 0),
                new Game.OffSet(-1, 0)
        };

        for (var o : offsets){
            RCP.addControlSquares(squares, this.side, pos, board, o);
        }
    }
}
