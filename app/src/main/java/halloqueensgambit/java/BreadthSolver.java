package halloqueensgambit.java;

public class BreadthSolver {
    Game game;
    int turn;

    public BreadthSolver(Game game){
        this.game = game;
        this.turn = 0;
    }

    public BreadthSolver(String boardFilePath){
        this.game = new Game(boardFilePath);
        this.turn = 0;
    }

    public int evaluatePosition(){
        return game.evaluateBoard();
    }
}
