package halloqueensgambit.java;

import java.util.Optional;
import halloqueensgambit.java.Game.Pos;

import halloqueensgambit.java.piece.Piece;

public class Solver {
    Game game;
    private Move bestMove = null;
    static int numPositionsSeen = 0;
    private int originalDepth;

    public Solver(Game game){
        this.game = game;
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
        //If depth reaches 0 or king has been taken: return the game evaluation
        if (depth == 0 || Math.abs(this.game.evaluation()) > 100){
            numPositionsSeen ++;
            return this.game.evaluation();
        }

        Side currentSide = this.game.side();
        //TODO: if you have no next moves, check if stalemate, thus eval would be 0 and not these
        int eval = (currentSide == Side.WHITE) ? -10000000 : 1000000;
        for(Move m: this.game.getLegalMoves()){
            Optional<Piece> captured = this.game.makeMove(m);
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
}