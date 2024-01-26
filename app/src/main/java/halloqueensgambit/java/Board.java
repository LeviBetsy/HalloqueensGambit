package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;
import halloqueensgambit.java.Game.Pos;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static halloqueensgambit.java.IO.scanPiece;


public class Board implements Iterable<Map.Entry<Pos, Piece>> {
    /*                           FIELDS AND CONSTRUCTORS                           */
    public TreeMap<Pos, Piece> data;
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
