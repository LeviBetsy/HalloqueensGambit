package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;
import halloqueensgambit.java.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
        return Game.knightValue*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♞ ";
        else
            return " ♘ ";
    }

    @Override
    public void addLegalMoves(List<Move> moves, Set<Pos> pinnedPath, Pos pos, Game game){
        Board board = game.board();
        for (Pos nextPos : knightSquares(pos)){
            //can only move to empty square or enemy square
            //if you're pinned as a knight, you can't really go anywhere
            if (board.notAlly(nextPos, this.side) && pinnedPath == null){
                moves.add(new Move(pos, nextPos));
            }
        }
    }

    @Override
    public void addControllingSquares(Set<Pos> squares, Pos pos, Board board) {
        squares.addAll(knightSquares(pos));
    }

    //                                  HELPER FUNCTIONS
    private List<Pos> knightSquares(Pos pos){
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
        return Arrays.stream(nextPos).filter(Game::inBound).collect(Collectors.toList());
    }
}
