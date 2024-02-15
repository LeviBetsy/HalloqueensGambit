package halloqueensgambit.java;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import halloqueensgambit.java.Game.Pos;

import halloqueensgambit.java.piece.Piece;

public class Solver {
    Game game;
    private Move bestMove = null;
    static int numPositionsSeen = 0;
    private int originalDepth;

    public Solver(Game game, int originalDepth){
        this.game = game;
        this.originalDepth = originalDepth;
    }

    public Move bestMove(){
        return this.bestMove;
    }

    public int solve(int depth){
        numPositionsSeen = 0;
        this.originalDepth = depth;
        return search(depth);
    }

    public int search(int depth){
        List<Move> allLegalMoves = this.game.getLegalMoves();
        Side currentSide = this.game.side();

        if (allLegalMoves.isEmpty()) {
            if (this.game.inCheck())
                return (currentSide == Side.WHITE) ? -10000000 : 1000000;
            else
                return 0;
        }

        if (depth == 0){
            return this.game.evaluation();
        }

        int eval = (currentSide == Side.WHITE) ? -10000000 : 1000000;
        for(Move m: this.game.getLegalMoves()){
            Optional<Piece> captured = this.game.makeMove(m);
            numPositionsSeen++;

            int newEval = search(depth - 1);
            //selecting the best move
            if (currentSide == Side.WHITE) {
                 if (newEval > 10000) {
                     eval = newEval;
                     if (depth == originalDepth)
                        this.bestMove = m;
                     this.game.unMakeMove(m, captured);
                     break;
                 } else if (newEval > eval){
                     eval = newEval;
                     if (depth == originalDepth)
                        this.bestMove = m;
                 }
            } else {
                if (newEval < -10000) {
                    eval = newEval;
                    if (depth == originalDepth)
                        this.bestMove = m;
                    this.game.unMakeMove(m, captured);
                    break;
                } else if (newEval < eval){
                    eval = newEval;
                    if (depth == originalDepth)
                        this.bestMove = m;
                }
            }
            this.game.unMakeMove(m, captured);
        }
        return eval;
    }

    public int alphaBetaSearch(int depth, int alpha, int beta){
        List<Move> allLegalMoves = this.game.getLegalMoves();
        Side currentSide = this.game.side();

        if (allLegalMoves.isEmpty()) {
            if (this.game.inCheck())
                return (currentSide == Side.WHITE) ? -10000000 : 1000000;
            else
                return 0;
        }

        int thisAlpha = alpha;
        int thisBeta = beta;

        if (depth == 0){
            return this.game.evaluation();
        } else if (this.game.side() == Side.WHITE){
            int maxEval = -1000000000;
            for (Move m : allLegalMoves){
                var temp = game.makeMove(m);
                int eval = alphaBetaSearch(depth - 1, thisAlpha, thisBeta);
                if (eval > 100) {
                    game.unMakeMove(m, temp);
                    if (depth == originalDepth)
                        this.bestMove = m;
                    return eval;
                }
                if (eval > maxEval){
                    maxEval = eval;
                    if (depth == originalDepth)
                        this.bestMove = m;
                }
                //you can't have it be the max between  maxEval bc your alpha gets passed down.
                thisAlpha = Math.max(thisAlpha, eval);
                game.unMakeMove(m, temp);
                //if the biggest it has is larger than the smallest its opponent got (the beta that was passed down)
                //its opponent won't take the path
                if (thisBeta <= thisAlpha)
                    break;
            }
            return maxEval;
        } else {
            int minEval = 1000000000;
            for (Move m : allLegalMoves){
                var temp = game.makeMove(m);
                int eval = alphaBetaSearch(depth - 1, thisAlpha, thisBeta);
                if (eval < -100) {
                    game.unMakeMove(m, temp);
                    if (depth == originalDepth)
                        this.bestMove = m;
                    return eval;
                }
                if (eval < minEval){
                    minEval = eval;
                    if (depth == originalDepth)
                        this.bestMove = m;
                }
                thisBeta = Math.min(thisBeta, eval);
                game.unMakeMove(m, temp);
                if (thisBeta <= thisAlpha)
                    break;
            }
            return minEval;
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