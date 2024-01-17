package halloqueensgambit.java;

import halloqueensgambit.java.Game.*;
import halloqueensgambit.java.piece.Piece;
import java.util.TreeMap;

public class Board {
    private TreeMap<Pos, Piece> data;
    public Board(TreeMap<Pos, Piece> data){
        this.data = data;
    }

    public Board modifyBoard(Move move){
        TreeMap<Pos, Piece> newData = new TreeMap<>(data);
        Piece movingPiece = newData.remove(move.start());
        newData.put(move.end(), movingPiece);
        return new Board(newData);
    }

    //TODO: maybe change to StringBuilder for optimization
    @Override
    public String toString(){
        String result = "";
        for (int y = 8; y >= 1; y--){
            for (int x = 1; x <= 8; x++){
                Piece current = data.get(new Pos(x, y));

                if (current != null) {
                    result += current.toString();
                } else {
                    result += "   ";
                }

                if (x != 8)
                    result += "|";
            }
            if (y != 1) {
                result += "\n";
                result += "-------------------------------";
                result += "\n";
            }
        }
        return result;
    }
}
