package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;
import halloqueensgambit.java.Game.Pos;

import java.util.TreeMap;
import java.util.Map;
import java.util.Optional;
import java.util.Iterator;

public class Board implements Iterable<Map.Entry<Pos, Piece>> {
    /*                           FIELDS AND CONSTRUCTORS                           */
    private TreeMap<Pos, Piece> data;
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
    public Board makeMove(Game.Move move){
        TreeMap<Pos, Piece> newData = new TreeMap<>(data);
        newData.remove(move.start());
        //PROMOTION
        if (move.pieceAfterMove() instanceof Pawn) {
            if (move.pieceAfterMove().side() == Side.WHITE && move.start().y() == 7 && move.end().y() == 8){
                newData.put(move.end(), new Queen(Side.WHITE, move.end()));
            } else if (move.pieceAfterMove().side() == Side.BLACK && move.start().y() == 2 && move.end().y() == 1){
                newData.put(move.end(), new Queen(Side.BLACK, move.end()));
            } else {
                newData.put(move.end(), move.pieceAfterMove());
            }
        } 
        //CASTLING
        else if (move.pieceAfterMove() instanceof King) {
            //CASTLING WHITE QUEEN SIDE
            if (move.pieceAfterMove().side() == Side.WHITE && move.start().equals(new Pos(5,1) )
                    && move.end().equals(new Pos(3,1))){
                //MOVING THE ROOK
                newData.remove(new Pos(1,1));
                newData.put(new Pos(4,1), new Rook(Side.WHITE, new Pos(4,1), true));
                newData.put(move.end(), move.pieceAfterMove());
            //CASTLING WHITE KING SIDE
            } else if (move.pieceAfterMove().side() == Side.WHITE && move.start().equals(new Pos(5,1) )
                    && move.end().equals(new Pos(7,1))){
                //MOVING THE ROOK
                newData.remove(new Pos(8,1));
                newData.put(new Pos(6,1), new Rook(Side.WHITE, new Pos(6,1), true));
                newData.put(move.end(), move.pieceAfterMove());
            //CASTLING BLACK QUEEN SIDE
            } else if (move.pieceAfterMove().side() == Side.BLACK && move.start().equals(new Pos(5,8) )
                    && move.end().equals(new Pos(3,8))){
                //MOVING THE ROOK
                newData.remove(new Pos(1,8));
                newData.put(new Pos(4,8), new Rook(Side.BLACK, new Pos(4,8), true));
                newData.put(move.end(), move.pieceAfterMove());
            //CASTLING BLACK KING SIDE
            } else if (move.pieceAfterMove().side() == Side.BLACK && move.start().equals(new Pos(5,8) )
                    && move.end().equals(new Pos(7,8))) {
                //MOVING THE ROOK
                newData.remove(new Pos(8, 8));
                newData.put(new Pos(6, 8), new Rook(Side.BLACK, new Pos(6, 8), true));
                newData.put(move.end(), move.pieceAfterMove());
            } else {
                newData.put(move.end(), move.pieceAfterMove());
            }
        } else {
            newData.put(move.end(), move.pieceAfterMove());
        }
        return new Board(newData);
    }

    //TODO: Stringbuilder
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
