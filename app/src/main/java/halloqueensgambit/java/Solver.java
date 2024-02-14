package halloqueensgambit.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import halloqueensgambit.java.piece.Piece;

public class Solver {
    Game game;
    private Move bestMove = null;
    static int numPositionsSeen = 0;

    public Solver(Game game){
        this.game = game;
    }

    public Move bestMove(){
        return this.bestMove;
    }

    public int solve(int depth){
        numPositionsSeen = 0;
        return search(depth);
    }

    public int search(int depth){
        if (depth == 0 || this.game.evaluation() > 100){
            numPositionsSeen ++;
            return this.game.evaluation();
        }

        Side currentSide = this.game.side();
        //TODO: if you have no next moves, check if stalemate, thus eval would be 0 and not these
        int eval = (currentSide == Side.WHITE) ? -10000 : 10000;
        for(Move m: this.game.getLegalMoves()){
            Optional<Piece> captured = this.game.makeMove(m);
            int newEval = search(depth - 1);

            if (currentSide == Side.WHITE) {
                 if (newEval > 10000) {
                     eval = newEval;
                     this.bestMove = m;
                     break;
                 } else if (newEval > eval){
                     eval = newEval;
                     this.bestMove = m;
                 }
            } else {
                if (newEval < -10000) {
                    eval = newEval;
                    this.bestMove = m;
                    break;
                } else if (newEval < eval){
                    eval = newEval;
                    this.bestMove = m;
                }
            }
            this.game.unMakeMove(m, captured);
        }
        return eval;
    }

//    private int selectMove(Side currentSide, List<Integer> evals, int index){
//        var eval = evals.get(index);
//        if (index == evals.size() - 1){
//            return eval;
//        } else if (currentSide == Side.WHITE){
//            if (eval > 100)
//                return eval;
//            else
//                return Math.max(eval, selectMove(currentSide, evals, index + 1));
//        } else {
//            if (eval < -100)
//                return eval;
//            else
//                return Math.min(eval, selectMove(currentSide, evals, index + 1));
//        }
//    }
}
