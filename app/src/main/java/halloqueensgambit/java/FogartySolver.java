package halloqueensgambit.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import halloqueensgambit.java.Game.Move;

public class FogartySolver {
    public record moveRating(Move m, int rating){}
    private static moveRating moveEstimate(Game game, int depth){
        if (depth == 0 || !game.hasBothKing()){
            return new moveRating(null, game.evaluateBoard());
        } else {
            Map<Move, moveRating> gameMove = new HashMap<>();
            for (Move m : game.getLegalMoves()){
                gameMove.put(m, moveEstimate(game.makeMove(m),depth - 1));
            }
            if (game.getSide() == Side.WHITE){
                moveRating bestMove = new moveRating(null, -10000000);
                for (var entry : gameMove.entrySet()){
                    if (entry.getValue().rating > bestMove.rating){
                        bestMove = new moveRating(entry.getKey(), entry.getValue().rating);
                    }
                }
                return bestMove;
            } else {
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

    public static moveRating bestMove(Game game, int depth){
        return moveEstimate(game, depth);
    }
}


