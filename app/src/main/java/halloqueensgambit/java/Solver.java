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
        int eval = search(4);
        System.out.println(eval);
        System.out.println("Num positions seen: " + numPositionsSeen);
        numPositionsSeen = 0;
        return eval;
    }

    public int search(int depth){
        if (depth == 0){
            numPositionsSeen ++;
            return this.game.evaluateBoard();
        }

        int eval = this.game.evaluateBoard();
        // Move bestMove = moves.get(0);
        for(Move m: this.game.getLegalMoves()){
            Optional<Piece> captured = this.game.makeMove(m);
            int newEval = - search(depth - 1);
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
