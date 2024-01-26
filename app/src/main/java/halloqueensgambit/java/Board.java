package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;
import halloqueensgambit.java.Game.Pos;

import java.util.TreeMap;
import java.util.Map;
import java.util.Optional;
import java.util.Iterator;

public class Board implements Iterable<Map.Entry<Pos, Piece>> {
    /*                           FIELDS AND CONSTRUCTORS                           */
    public TreeMap<Pos, Piece> data;
    public Board(TreeMap<Pos, Piece> data){
        this.data = data;
    }
    public Board() {
        this.data = new TreeMap<>();
    }

    /*                               METHODS                           */

    //Any use of the Board's iterator is risking modifying the underlying tree
    public Iterator<Map.Entry<Pos, Piece>> iterator() {
        return data.entrySet().iterator();
    }

    public boolean hasPiece(Piece p){
        return this.data.containsValue(p);
    }

    public void addToBoard(Pos pos, Piece piece){
        this.data.put(pos, piece);
    }

    public Optional<Piece> lookupBoard(Pos pos){
        Piece p = this.data.get(pos);
        if (p == null){
            return Optional.empty();
        } else {
            return Optional.of(p);
        }
    }

    //if square is empty or not the same side, then return true
    public boolean notAlly(Pos pos, Side side){
        Piece p = this.data.get(pos);
        if (p == null){
            return true;
        } else {
            return p.side() != side;
        }
    }

    public int evaluate(){
        int eval = 0;
        // iterate through the board
        for(Map.Entry<Game.Pos,Piece> entry: this){ 
            eval += entry.getValue().value(); 
        }
        return eval;
    }

    //TODO: maybe change to StringBuilder for optimization
    @Override
    public String toString(){
        String result = "";
        for (int y = 8; y >= 1; y--){
            result += Integer.toString(y) + "  ";
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
                result += "\n" + "   -------------------------------" + "\n";
            }
        }
        return result;
    }
}
