package halloqueensgambit.java;

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

    public int evaluatePosition(){
        return game.evaluateBoard();
    }
}

