package halloqueensgambit.java;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import halloqueensgambit.java.Game.Pos;

import halloqueensgambit.java.piece.King;
import halloqueensgambit.java.piece.Piece;

public class Solver {
    Game game;
    public static int numPositionsSeen = 0;
    public static record MoveRating(Move move, int rating){};

    public Solver(Game game){
        this.game = game;
    }

//    public int solve(int depth){
//        numPositionsSeen = 0;
////        this.originalDepth = depth;
//        return search(depth);
//    }
//
//    public int search(int depth){
//        List<Move> allLegalMoves = this.game.getLegalMoves();
//        Side currentSide = this.game.side();
//
//        if (allLegalMoves.isEmpty()) {
//            if (this.game.inCheck())
//                return (currentSide == Side.WHITE) ? -Game.kingValue : Game.kingValue;
//            else
//                return 0;
//        }
//
//        if (depth == 0){
//            return this.game.evaluation();
//        }
//
//        int eval = (currentSide == Side.WHITE) ? -10000000 : 1000000;
//        for(Move m: this.game.getLegalMoves()){
//            Optional<Piece> captured = this.game.makeMove(m);
//            numPositionsSeen++;
//
//            int newEval = search(depth - 1);
//            //selecting the best move
//            if (currentSide == Side.WHITE) {
//                 if (newEval > 10000) {
//                     eval = newEval;
////                     if (depth == originalDepth)
//                        this.bestMove = m;
//                     this.game.unMakeMove(m, captured);
//                     break;
//                 } else if (newEval > eval){
//                     eval = newEval;
////                     if (depth == originalDepth)
//                        this.bestMove = m;
//                 }
//            } else {
//                if (newEval < -10000) {
//                    eval = newEval;
//                    if (depth == originalDepth)
//                        this.bestMove = m;
//                    this.game.unMakeMove(m, captured);
//                    break;
//                } else if (newEval < eval){
//                    eval = newEval;
//                    if (depth == originalDepth)
//                        this.bestMove = m;
//                }
//            }
//            this.game.unMakeMove(m, captured);
//        }
//        return eval;
//    }

    public MoveRating alphaBetaSearch(int depth, int alpha, int beta){
        List<Move> allLegalMoves = this.game.getLegalMoves();
        Side currentSide = this.game.side();

        if (allLegalMoves.isEmpty()) {
            if (this.game.inCheck())
                return new MoveRating(null, (currentSide == Side.WHITE) ? -Game.kingValue : Game.kingValue);
            else
                return new MoveRating(null, 0);
        }

        //so I better understand that alpha and beta is not being mutated
        int thisAlpha = alpha;
        int thisBeta = beta;

        //if the depth is 0 stop looking and return the evaluation
        if (depth == 0){
            return new MoveRating(null, this.game.evaluation());
        } else if (this.game.side() == Side.WHITE){
            int maxEval = -1000000000;
            MoveRating bestMoveRating = null;
            for (Move m : allLegalMoves){
                var temp = game.makeMove(m);
                int eval = alphaBetaSearch(depth - 1, thisAlpha, thisBeta).rating;
                //if there is a game state where we took the enemy king, just take it
                if (eval >= Game.kingValue/10) {
                    game.unMakeMove(m, temp);
                    return new MoveRating(m, eval);
                } else if (eval > maxEval){
                    maxEval = eval;
                    bestMoveRating = new MoveRating(m, eval);
                }
                thisAlpha = Math.max(thisAlpha, eval);
                game.unMakeMove(m, temp);
                if (thisBeta <= thisAlpha)
                    break;
            }
            return bestMoveRating;
        } else {
            int minEval = 1000000000;
            MoveRating bestMoveRating = null;
            for (Move m : allLegalMoves){
                var temp = game.makeMove(m);
                int eval = alphaBetaSearch(depth - 1, thisAlpha, thisBeta).rating;
                //if there is a game state where we took the enemy king, just take it
                if (eval < -Game.kingValue/10) {
                    game.unMakeMove(m, temp);
                    return new MoveRating(m, eval);
                }
                if (eval < minEval){
                    minEval = eval;
                    bestMoveRating = new MoveRating(m, eval);
                }
                thisBeta = Math.min(thisBeta, eval);
                game.unMakeMove(m, temp);
                if (thisBeta <= thisAlpha)
                    break;
            }
            return bestMoveRating;
        }
    }

    public void pureSearch(int depth){
        if (depth != 0) {
            for (Move m : this.game.getLegalMoves()){
                if (depth == 1)
                    numPositionsSeen++;
                var temp = this.game.makeMove(m);
                pureSearch(depth - 1);
                this.game.unMakeMove(m, temp);
            }
        }
    }

}