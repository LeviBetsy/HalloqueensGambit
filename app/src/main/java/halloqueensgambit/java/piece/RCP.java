package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.Optional;

public class RCP {
    public static ArrayList<Game.Pos> RecurCheckPath(ArrayList<Game.Pos> lst, Board b, Side currentSide, Game.Pos pos, Game.OffSet o){
        Game.Pos nextPos = new Game.Pos(pos.x() + o.dx(), pos.y() + o.dy());
        if (!Game.inBound(nextPos)){
           return lst;
        }

        Optional<Piece> currentSq = b.lookupBoard(nextPos);
        if (currentSq.isEmpty()){
            lst.add(nextPos);
            return RecurCheckPath(lst, b, currentSide, nextPos, o);
        } else {
          if (currentSq.get().side() != currentSide){
              lst.add(nextPos);
          }
          return lst;
        }
    }
}
