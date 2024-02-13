package halloqueensgambit.java;

import java.util.ArrayList;
import java.util.Optional;

import halloqueensgambit.java.piece.Piece;

public class Solver {
    Game game;
    static int numPositionsSeen = 0;

    public Solver(Game game){
        this.game = game;
    }

    public int solve(int depth){
        numPositionsSeen = 0;
        return search(depth);
    }

    public int search(int depth){
        if (depth == 0){
            numPositionsSeen ++;
            return this.game.evaluation();
        }

        int eval = this.game.evaluation();
        if (java.lang.Math.abs(eval) > 100) {
            numPositionsSeen ++;
            return eval;
        }

        for(Move m: this.game.getLegalMoves()){
            Optional<Piece> captured = this.game.makeMove(m);
            int newEval = search(depth - 1);
            if(this.game.side() == Side.WHITE && newEval > eval){
                eval = newEval;
            } else if (this.game.side() == Side.BLACK && newEval < eval){
                eval = newEval;
            }
            this.game.unMakeMove(m, captured);
        }
        return eval;
    }
}
