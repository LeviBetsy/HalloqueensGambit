package halloqueensgambit.java;

import java.util.HashMap;
import java.util.Map;

public class FogartySolver {
    static int positionsSeen = 0;
    
    public record moveRating(Move m, int rating){}

    public static moveRating exhaustive(Game game, int depth){
        positionsSeen ++;
        // base case
        if (depth == 0 || !game.hasBothKing()){
            return new moveRating(null, game.evaluateBoard());
        }

        // get all legal moves
        Map<Move, moveRating> gameMove = new HashMap<>();
        for (Move m : game.getLegalMoves()){
            Game tmp = game.copy();
            tmp.makeMove(m);
            gameMove.put(m, exhaustive(tmp, depth - 1));
        }
        
        // white
        if (game.getSide() == Side.WHITE){
            moveRating bestMove = new moveRating(null, -10000000);
            for (var entry : gameMove.entrySet()){
                if (entry.getValue().rating > bestMove.rating){
                    bestMove = new moveRating(entry.getKey(), entry.getValue().rating);
                }
            }
            return bestMove;
        }
        
        // black
        else {
            moveRating bestMove = new moveRating(null, 10000000);
            for (var entry : gameMove.entrySet()){
                if (entry.getValue().rating < bestMove.rating){
                    bestMove = new moveRating(entry.getKey(), entry.getValue().rating);
                }
            }
            return bestMove;
        }
    }
}


