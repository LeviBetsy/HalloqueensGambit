package halloqueensgambit.java;

import halloqueensgambit.java.Game;
import halloqueensgambit.java.piece.Pawn;
import halloqueensgambit.java.piece.Piece;
import halloqueensgambit.java.piece.Queen;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Iterator;

public class Board implements Iterable<Map.Entry<Game.Pos, Piece>> {
    /*                           FIELDS AND CONSTRUCTORS                           */
    private TreeMap<Game.Pos, Piece> data;
    public Board(TreeMap<Game.Pos, Piece> data){
        this.data = data;
    }
    public Board() {
        this.data = new TreeMap<>();
    }



    /*                               METHODS                           */

    //Any use of the Board's iterator is risking modifying the underlying tree
    public Iterator<Map.Entry<Game.Pos, Piece>> iterator() {
        return data.entrySet().iterator();
    }

    public boolean hasPiece(Piece p){
        return this.data.containsValue(p);
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
    public Board makeMove(Game.Move move){
        TreeMap<Game.Pos, Piece> newData = new TreeMap<>(data);
        Piece movingPiece = newData.remove(move.start());
        if (movingPiece instanceof Pawn) {
            if (move.p().side() == Side.WHITE && move.start().y() == 7 && move.end().y() == 8){
                newData.put(move.end(), new Queen(Side.WHITE, move.end()));
            } else if (move.p().side() == Side.BLACK && move.start().y() == 2 && move.end().y() == 1){
                newData.put(move.end(), new Queen(Side.BLACK, move.end()));
            } else {
                newData.put(move.end(), movingPiece);
            }
        } else {
            newData.put(move.end(), movingPiece);
        }
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
