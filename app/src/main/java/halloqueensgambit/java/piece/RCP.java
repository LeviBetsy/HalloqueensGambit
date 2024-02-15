package halloqueensgambit.java.piece;

import halloqueensgambit.java.Game;
import halloqueensgambit.java.Board;

import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Move;
import halloqueensgambit.java.Side;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

//RCP is short for recursive checking path
public class RCP {
    //take a position and keep adding onto a list the valid moves you can go to following this offset
    //path is blocked by ally piece, path includes the last square if it is your enemy
    //if piece is pinned, also account that its moves must follow the pinned line
    public static void recurAddMove(List<Move> lst, Set<Pos> pins, Board board, Side currentSide, Pos start, Pos current, Game.OffSet o){
        Pos nextPos = new Pos(current.x() + o.dx(), current.y() + o.dy());
        if (pins == null || pins.contains(nextPos)) {
            if (Game.inBound(nextPos)) {
                Optional<Piece> currentSq = board.lookup(nextPos);
                if (currentSq.isEmpty()) {
                    lst.add(new Move(start, nextPos));
                    recurAddMove(lst, pins, board, currentSide, start, nextPos, o);
                } else {
                    //if there is an obstacle, check if it's an enemy to add it on, otherwise stop
                    if (currentSq.get().side() != currentSide) {
                        lst.add(new Move(start, nextPos));
                    }
                }
            }
        }
    }

    //add onto a set, the squares you are controlling if you follow this offset
    //path is blocked by any piece and always includes the last square
    public static void addControlSquares(Set<Pos> set, Side side, Pos pos, Board board, Game.OffSet o){
        Pos nextPos = new Pos(pos.x() + o.dx(), pos.y() + o.dy());
        if (Game.inBound(nextPos)){
            Optional<Piece> currentSq = board.lookup(nextPos);
            if (currentSq.isEmpty()){
                set.add(nextPos);
                addControlSquares(set, side, nextPos, board, o);
            } else {
                set.add(nextPos);
                //if the blocking piece is enemy king
                //then you are also controlling the square directly after the king (so king cant move there)
                if (currentSq.get().side() != side && currentSq.get() instanceof King
                        && Game.inBound(nextPos.x() + o.dx(), nextPos.y() + o.dy())){
                    set.add(new Pos(nextPos.x() + o.dx(), nextPos.y() + o.dy()));
                }
            }
        }
    }

    //Adds to the set the path between the piece and the King
    public static void addCheckedPath(Set<Pos> set, Pos pos, Board board, Game.OffSet o){
        Pos nextPos = new Pos(pos.x() + o.dx(), pos.y() + o.dy());
        if (board.lookup(nextPos).isEmpty()){
            set.add(nextPos);
            addCheckedPath(set, nextPos, board, o);
        }
    }

    //add a pair of (Pinned piece : Set of positions it must follow) to a map
    //pinned path also includes position of the enemy making the pin as it
    //pins can only be made by enemy Bishop, Queen or Rook
    public static void pinningPath(boolean isDiagonal, Map<Piece, Set<Pos>> map, Set<Pos> path, Game game, Optional<Piece> foundAlly, Pos pos, Game.OffSet o){
        Board board = game.board();
        Side side = game.side();
        Pos nextPos = new Pos(pos.x() + o.dx(), pos.y() + o.dy());
        if (Game.inBound(nextPos)){
            Optional<Piece> currentSq = board.lookup(nextPos);
            if (currentSq.isEmpty()){
                path.add(nextPos);
                pinningPath(isDiagonal, map, path, game, foundAlly, nextPos, o);
            } else if (currentSq.get().side() != side){
                //if it's an enemy and there was an ally in between it is a pin
                //otherwise it's a check so ignore
                if (foundAlly.isPresent()){
                    if (isDiagonal && (currentSq.get() instanceof Bishop || currentSq.get() instanceof Queen)) {
                        path.add(nextPos);
                        map.put(foundAlly.get(), path);
                    } else if (!isDiagonal && (currentSq.get() instanceof Rook || currentSq.get() instanceof Queen)){
                        path.add(nextPos);
                        map.put(foundAlly.get(), path);
                    }
                }
            } else {
                //if it's an ally and there was an ally in between then it's not a pin
                //otherwise keep searching with the ally in mind
                if (foundAlly.isEmpty()){
                    pinningPath(isDiagonal, map, path, game, currentSq, nextPos, o);
                }
            }
        }
    }
}
