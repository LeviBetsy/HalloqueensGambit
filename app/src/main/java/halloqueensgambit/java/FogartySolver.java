package halloqueensgambit.java;

import java.util.ArrayList;

public class FogartySolver {
    Game game;
    int turn;

    public FogartySolver(Game game){
        this.game = game;
        this.turn = 0;
    }

    public FogartySolver(String boardFilePath){
        this.game = new Game(boardFilePath);
        this.turn = 0;
    }

    public int evaluatePosition(int depth){
        int eval = 0;
        
        // base case
        if(depth == 0){
            return game.evaluateBoard();
        }
        // get legal moves
        ArrayList<Game.Move> legalMoves = game.getLegalMoves();
        for(Game.Move m: legalMoves){
            // make the move, find the result and compare to the current eval
            game.makeMove(m);
            int eval2 = - evaluatePosition(depth-1); // negative because opponents turn
            eval = Math.max(eval, eval2);
            game.unMakeMove(m);
        }
        
        return eval;
    }
}

