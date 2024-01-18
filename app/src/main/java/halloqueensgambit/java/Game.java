package halloqueensgambit.java;
import halloqueensgambit.java.piece.Piece;

public class Game {
    private Side side;
    private Board board;
    private int turn;
    public Game(Side side, Board board, int turn){
        this.side = side;
        this.board = board;
        this.turn = turn;
    }
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
    public static record Move(Pos start, Pos end){};

    //TODO: Stringbuilder and make this look better
    @Override
    public String toString(){
        String result = "";
        result += "Turn(s) remaining: " + Integer.toString(this.turn) + "\n";
        result += "Current player: " + this.side.toString() + "\n";
        result += this.board.toString();
        return result;
    }


    //we do need to make a new board everytime, thus a hard copy, shitting

}
