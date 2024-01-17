package halloqueensgambit.java.piece;
import java.util.ArrayList;
import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game.*;
import halloqueensgambit.java.Side;
public interface Piece {
    //not keeping track of x, y for each piece internally because we are working w AVL Tree
    Side side();
    int value();
    ArrayList<Move> allLegalMove(Pos start, Board board);
}
