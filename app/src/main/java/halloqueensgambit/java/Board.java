package halloqueensgambit.java;

import halloqueensgambit.java.Game;
import halloqueensgambit.java.piece.Piece;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Board {
    /*                           FIELDS AND CONSTRUCTORS                           */
    private TreeMap<Game.Pos, Piece> data;
    public Board(TreeMap<Game.Pos, Piece> data){
        this.data = data;
    }
    public Board() {
        this.data = new TreeMap<>();
    }


    /*                               METHODS                           */
    public ArrayList<Game.Move> legalMoves(Side side){
        ArrayList<Game.Move> result = new ArrayList<>();
        for (Map.Entry<Game.Pos, Piece> entry : data.entrySet()) {
            if (entry.getValue().side() == side){
                //TODO: might be slow?
                //add the piece.allLegalMove(position, board) to the result
                result.addAll(entry.getValue().allLegalMove(entry.getKey(), this));
            }
        }
        return result;
    }
    public void addToBoard(Game.Pos pos, Piece piece){
        this.data.put(pos, piece);
    }
    public Optional<Piece> lookupBoard(Game.Pos pos){
        Piece p = this.data.get(pos);
        if (p == null){
            return Optional.empty();
        } else {
            return Optional.of(p);
        }
    }

    //if square is empty or not the same side, then return true
    public boolean notAlly(Game.Pos pos, Side side){
        Piece p = this.data.get(pos);
        if (p == null){
            return true;
        } else {
            return p.side() != side;
        }
    }
    public Board modifyBoard(Game.Move move){
        //TODO: PAWN PROMOTION
        TreeMap<Game.Pos, Piece> newData = new TreeMap<>(data);
        Piece movingPiece = newData.remove(move.start());
        newData.put(move.end(), movingPiece);
        return new Board(newData);
    }

    //TODO: maybe change to StringBuilder for optimization
    @Override
    public String toString(){
        String result = "";
        for (int y = 8; y >= 1; y--){
            result += Integer.toString(y) + "  ";
            for (int x = 1; x <= 8; x++){
                Piece current = data.get(new Game.Pos(x, y));
                if (current != null) {
                    result += current.toString();
                } else {
                    result += "   ";
                }
                if (x != 8)
                    result += "|";
            }
            if (y != 1) {
                result += "\n" + "   -------------------------------" + "\n";
            }
        }
        return result;
    }
}
