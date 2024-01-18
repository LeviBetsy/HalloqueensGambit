package halloqueensgambit.java.piece;
import java.util.ArrayList;
import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;
public interface Piece {
    //not keeping track of x, y for each piece internally because we are working w AVL Tree
    //but if we don't keep track of x, y for each piece internally, when we want to find all legal moves
    //in a game state, we loop through all the pieces that are a color, then we pass their position into themselves
    //I suppose it isn't that bad. There's always a way to make something different. Let's just go with it
    Side side();
    int value();
    ArrayList<Game.Move> allLegalMove(Game.Pos start, Board board);


}


