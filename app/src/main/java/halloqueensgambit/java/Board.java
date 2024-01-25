package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;
import halloqueensgambit.java.Game.Pos;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static halloqueensgambit.java.IO.scanPiece;


public class Board implements Iterable<Map.Entry<Pos, Piece>> {
    /*                           FIELDS AND CONSTRUCTORS                           */
    private TreeMap<Pos, Piece> data;
    public Board(TreeMap<Pos, Piece> data){
        this.data = data;
    }
    public Board() {
        this.data = new TreeMap<>();
    }

    public Board(String fileName) {
        try{
            // Resolve the file path
            String currentDirectory = System.getProperty("user.dir");
            // Connect the filepath
            Path filePath = Paths.get(currentDirectory, "src/main/java/halloqueensgambit/java/games", fileName);
            Scanner scanner = new Scanner(filePath);

            //ignore the side information
            scanner.nextLine();

            for (int y = 8; y >= 1; y--){
                String row = scanner.nextLine();
                //splitting a row into individual squares
                String[] squares = row.split("\\s+");
                for (int x = 1; x <= 8; x++){
                    Optional<Piece> currentPiece = scanPiece(squares[x - 1], new Pos(x,y));
                    //if scan Piece does not return an Optional value
                    if (currentPiece.isPresent()){
                        this.addToBoard(new Pos(x,y), currentPiece.get());
                    }
                }
            }
            scanner.close();
        } catch(Exception e){
            System.out.println("Unable to read board: " + e.getLocalizedMessage());
        }
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
        Piece movingPiece = newData.remove(move.start());
        if (movingPiece instanceof King){
            ((King) movingPiece).hasMoved = true;
        } else if (movingPiece instanceof Rook){
            ((Rook) movingPiece).hasMoved = true;
        }

        //PROMOTION
        if (movingPiece instanceof Pawn) {
            if (movingPiece.side() == Side.WHITE && move.start().y() == 7 && move.end().y() == 8){
                newData.put(move.end(), new Queen(Side.WHITE));
            } else if (movingPiece.side() == Side.BLACK && move.start().y() == 2 && move.end().y() == 1){
                newData.put(move.end(), new Queen(Side.BLACK));
            } else {
                newData.put(move.end(), movingPiece);
            }
        } 
        //CASTLING
        //TODO: abstract
        else if (movingPiece instanceof King) {
            //CASTLING WHITE QUEEN SIDE
            if (movingPiece.side() == Side.WHITE && move.start().equals(new Pos(5,1) )
                    && move.end().equals(new Pos(3,1))){
                //MOVING THE ROOK
                newData.remove(new Pos(1,1));
                newData.put(new Pos(4,1), new Rook(Side.WHITE, true));
                newData.put(move.end(), movingPiece);
            //CASTLING WHITE KING SIDE
            } else if (movingPiece.side() == Side.WHITE && move.start().equals(new Pos(5,1) )
                    && move.end().equals(new Pos(7,1))){
                //MOVING THE ROOK
                newData.remove(new Pos(8,1));
                newData.put(new Pos(6,1), new Rook(Side.WHITE, true));
                newData.put(move.end(), movingPiece);
            //CASTLING BLACK QUEEN SIDE
            } else if (movingPiece.side() == Side.BLACK && move.start().equals(new Pos(5,8) )
                    && move.end().equals(new Pos(3,8))){
                //MOVING THE ROOK
                newData.remove(new Pos(1,8));
                newData.put(new Pos(4,8), new Rook(Side.BLACK, true));
                newData.put(move.end(), movingPiece);
            //CASTLING BLACK KING SIDE
            } else if (movingPiece.side() == Side.BLACK && move.start().equals(new Pos(5,8) )
                    && move.end().equals(new Pos(7,8))) {
                //MOVING THE ROOK
                newData.remove(new Pos(8, 8));
                newData.put(new Pos(6, 8), new Rook(Side.BLACK, true));
                newData.put(move.end(), movingPiece);
            } else {
                newData.put(move.end(), movingPiece);
            }
        } else {
            newData.put(move.end(), movingPiece);
        }
        return new Board(newData);
    }

    public int numPiece(){
        return this.data.size();
    }

    public int evaluate(){
        int eval = 0;
        // iterate through the board
        for(Map.Entry<Game.Pos,Piece> entry: this){ 
            eval += entry.getValue().value(); 
        }
        return eval;
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
