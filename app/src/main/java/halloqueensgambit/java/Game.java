package halloqueensgambit.java;
import halloqueensgambit.java.piece.King;
import halloqueensgambit.java.piece.Piece;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;

public class Game {
    private Side side;
    private Board board;
    private int turn;
    public Game(Side side, Board board, int turn){
        this.side = side;
        this.board = board;
        this.turn = turn;
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
    public static record Move(Piece p, Pos start, Pos end){};

    public static record OffSet(int dx, int dy){};

    /*                                METHODS                                */

    public static boolean inBound(Game.Pos pos){
        return (pos.x() >= 1 && pos.x() <= 8 && pos.y() >= 1 && pos.y() <= 8);
    }

    public boolean kingTaken(){
        return (this.board.hasPiece(new King(Side.WHITE)) && this.board.hasPiece(new King(Side.BLACK)));
    }



    public Optional<Winner> whoHasWon(){
        if (!this.board.hasPiece(new King(Side.WHITE))){
            return Optional.of(Winner.BLACK);
        } else if (!this.board.hasPiece(new King(Side.BLACK))){
            return Optional.of(Winner.WHITE);
        } else if (this.turn == 0){
            //what is a stalemate?, king is not checked, but there is nowhere to go.
            //a game state is not won, but all next game states are loss. NO
            //so if there's always a move, but that move.

            // a legal move means moving and the next one is not EXHAUSTIVE won.
            // a stalemate means no legal move
            // a RIGHTFUL WIN is stalemate and next one, same turn is EXHAUSTIVE won. THAT"S GOING TO BE SLOW. VERY SLOW.
            // one more layer of states. Maybe that is the only way,
            //TODO: stalemate check so they avoid a stalemate, aka a TIE, or push for it
            //you don't have to think about legalMove that expose king because the next move would be a take
            return Optional.of(Winner.TIE);
        } else {
            return Optional.empty();
        }
    }

    public ArrayList<Move> legalMoves(){
        ArrayList<Game.Move> result = new ArrayList<>();
        for (Map.Entry<Game.Pos, Piece> entry : this.board) {
            if (entry.getValue().side() == this.side){
                //TODO: might be slow?
                //add the piece.allLegalMove(position, board) to the result
                result.addAll(entry.getValue().allLegalMove(entry.getKey(), this.board));
            }
        }
        return result;
    }

    public ArrayList<Game> nextGames(){
        ArrayList<Game> result = new ArrayList<>();
        for (Move m : this.legalMoves()){
            Board nextBoard = this.board.modifyBoard(m);
            if (this.side == Side.BLACK){
                result.add(new Game(Side.WHITE, nextBoard, this.turn - 1));
            } else {
                result.add(new Game(Side.BLACK, nextBoard, this.turn - 1));
            }
        }
        return result;
    }

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
