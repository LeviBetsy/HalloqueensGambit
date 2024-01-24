package halloqueensgambit.java.piece;
import java.util.ArrayList;
import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game.Move;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Side;
public interface Piece {
    //keeping track of each piece's Pos means being able to generate legal moves using just the piece and the board
    //not keeping track means generating legal moves by passing the piece's pos to its function
    Side side();
    int value();
    ArrayList<Move> allLegalMove(Pos pos, Board board);
}


