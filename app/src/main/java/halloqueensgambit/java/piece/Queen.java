package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;
import halloqueensgambit.java.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    public void addLegalMoves(List<Move> moves, Set<Pos> pinnedPath, Pos pos, Game game){
        for (Game.OffSet o : Game.allOffset)
            RCP.recurAddMove(moves, pinnedPath, game.board(), this.side, pos, pos, o);
    }

    @Override
    public void addControllingSquares(Set<Pos> squares, Pos pos, Board board) {

        for (var o : Game.allOffset){
            RCP.addControlSquares(squares, pos, board, o);
        }
    }
}
