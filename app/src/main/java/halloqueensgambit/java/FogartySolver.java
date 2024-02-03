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
            gameMove.put(m, exhaustive(game.makeMove(m), depth - 1));
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
    
    public static moveRating monteCarlo(Game game, int currentDepth, int targetDepth, int maxDepth){
        // base case
        if (currentDepth >= targetDepth || !game.hasBothKing()){
            positionsSeen ++;
            if(positionsSeen % 1000000 == 0){System.out.println(positionsSeen + " positions seen.");}
            return new moveRating(null, game.evaluateBoard());
        }
    
        // get all legal moves
        Map<Move, moveRating> gameMove = new HashMap<>();
        for (Move m : game.getLegalMoves()){
            Game tmp = game.makeMove(m);

            int boardEval = tmp.evaluateBoard();
            
            // if our position is good for us, expand our search depth (max 10)
            if(boardEval * game.getSide().rateMult > 1){
                targetDepth = Math.min(targetDepth + 1, maxDepth);
            }

            gameMove.put(m, monteCarlo(tmp, currentDepth + 1, targetDepth, maxDepth));
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


