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
        if(depth == 0){
            numPositionsSeen ++;
            return this.game.evaluateBoard();
        }

        ArrayList<Move> moves = game.getLegalMoves();

        int eval = this.game.getSide().rateMult * -10000;
        // Move bestMove = moves.get(0);
        for(Move m: moves){
            Optional<Piece> captured = this.game.makeMove(m);
            int newEval = - search(depth - 1);
            if(newEval > eval){
                eval = newEval;
                // bestMove = m;
            }

            this.game.unMakeMove(m, captured);
        }

        return eval;
    }
}
