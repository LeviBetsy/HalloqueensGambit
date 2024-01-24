package halloqueensgambit.java;
import halloqueensgambit.java.piece.King;
import halloqueensgambit.java.piece.Piece;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;

public class Game {
    private Side side;
    private Board board;
    public Game(Side side, Board board){
        this.side = side;
        this.board = board;
    }



    /*                           DATATYPE                           */
    public static record Pos(int x, int y) implements Comparable<Pos> {
        @Override
        public int compareTo(Pos other) {
            // Your comparison logic here
            // For example, compare based on x first, then y
            int xComparison = Integer.compare(this.x, other.x);
            if (xComparison != 0) {
                return xComparison;
            }
            return Integer.compare(this.y, other.y);
        }
    }

    //need to have a record of Move to check whether player's move is legal or not
    public static record Move(Piece pieceAfterMove, Pos start, Pos end){};

    public static record OffSet(int dx, int dy){};

    /*                                METHODS                                */

    //RETURN THE SIDE WHICH HAS TAKEN THE OPPONENT'S KING
    public Optional<Side> whoHasWon(){
        boolean hasWhiteKing = false;
        boolean hasBlackKing = false;
        for (var entry : this.board){
            if (entry.getValue().side() == Side.WHITE && entry.getValue() instanceof King)
                hasWhiteKing = true;
            if (entry.getValue().side() == Side.BLACK && entry.getValue() instanceof King)
                hasBlackKing = true;
        }
        if (hasWhiteKing && hasBlackKing){
            return Optional.empty();
        } else if (hasWhiteKing){
            return Optional.of(Side.WHITE);
        } else {
            return Optional.of(Side.BLACK);
        }
    }
    public static boolean inBound(Game.Pos pos){
        return (pos.x() >= 1 && pos.x() <= 8 && pos.y() >= 1 && pos.y() <= 8);
    }

    public ArrayList<Game> allNextGames(){
        ArrayList<Game.Move> allLegalMoves = new ArrayList<>();
        for (Map.Entry<Game.Pos, Piece> entry : this.board) {
            if (entry.getValue().side() == this.side){
                allLegalMoves.addAll(entry.getValue().allLegalMove(this.board));
            }
        }

        ArrayList<Game> result = new ArrayList<>();
        for (Move m : allLegalMoves){
            Board nextBoard = this.board.makeMove(m);
            if (this.side == Side.BLACK){
                result.add(new Game(Side.WHITE, nextBoard));
            } else {
                result.add(new Game(Side.BLACK, nextBoard));
            }
        }
        return result;
    }



    //TODO: Stringbuilder and make this look better
    @Override
    public String toString(){
        String result = "";
        result += "Current player: " + this.side.toString() + "\n";
        result += this.board.toString();
        return result;
    }


    //we do need to make a new board everytime, thus a hard copy, shitting

}
