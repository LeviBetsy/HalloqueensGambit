package halloqueensgambit.java.piece;
import java.util.List;
import java.util.Set;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Move;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;
public interface Piece {
    //keeping track of each piece's Pos means being able to generate legal moves using just the piece and the board
    //not keeping track means generating legal moves by passing the piece's pos to its function
    Side side();
    int value();
    void addLegalMoves(List<Move> moves, Set<Pos> pinnedPath, Pos pos, Game game);
    //need to pass board to know if should keep checking
    void addControllingSquares(Set<Pos> squares, Pos pos, Board board);
    String toLetter();
}


