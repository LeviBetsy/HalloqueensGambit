package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;
import halloqueensgambit.java.Game.Pos;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Map;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Optional;

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
                    Optional<Piece> currentPiece = scanPiece(squares[x - 1]);
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

    public static Board fromFEN(String fen){
        String[] rows = fen.split("/");

        Board board = new Board();
        
        HashMap<Character, Integer> digits = new HashMap<>();
        for(int i = 49; i <= 56; i ++){
            digits.put((char) i, i-48);
        }

        // read the board
        for(int current_row = 1; current_row <= 8; current_row ++){
            int current_column = 1;
            for(char c: rows[8-current_row].toCharArray()){
                //check digit
                if(digits.containsKey(c)){
                    current_column += digits.get(c);
                }else{
                    Piece p = IO.scanPiece(Character.toString(c)).get();
                    board.addToBoard(new Game.Pos(current_column, current_row), p);
                    current_column ++;
                }
            }
        }

        return board;
    }

    /*                               METHODS                           */

    //Any use of the Board's iterator is risking modifying the underlying tree
    public Iterator<Map.Entry<Pos, Piece>> iterator() {
        return data.entrySet().iterator();
    }

    public void addToBoard(Pos pos, Piece piece){
        this.data.put(pos, piece);
    }
    public void addToBoard(int x, int y, Piece piece){
        this.data.put(new Pos(x,y), piece);
    }


    public Optional<Piece> lookup(int x, int y){
        Pos pos = new Pos(x, y);
        Piece p = this.data.get(pos);
        if (p == null){
            return Optional.empty();
        } else {
            return Optional.of(p);
        }
    }

    public Optional<Piece> lookup(Pos pos){
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

    @Override
    public boolean equals(Object obj){
        if(! (obj instanceof Board)) return false;
        Board other = (Board) obj;

        // check this one
        for(Map.Entry<Game.Pos,Piece> entry: this.data.entrySet()){
            Pos pos = entry.getKey();
            Optional<Piece> p = other.lookup(pos.x(), pos.y());
            if(p.equals(Optional.empty())) return false;
            if(p.get().value() != entry.getValue().value()) return false;
        }
        
        // check the other one
        for(Map.Entry<Game.Pos,Piece> entry: other.data.entrySet()){
            Pos pos = entry.getKey();
            Optional<Piece> p = this.lookup(pos.x(), pos.y());
            if(p.equals(Optional.empty())) return false;
            if(p.get().value() != entry.getValue().value()) return false;
        }
        
        return true;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        for (int y = 8; y >= 1; y--){
            sb.append(Integer.toString(y) + "  ");
            for (int x = 1; x <= 8; x++){
                Piece current = data.get(new Pos(x, y));
                if (current != null) {
                    sb.append(current.toString());
                } else {
                    sb.append("   ");
                }
                if (x != 8)
                    sb.append("|");
            }
            if (y != 1) {
                sb.append("\n" + "   -------------------------------" + "\n");
            }
        }
        sb.append("\n" + "    a   b   c   d   e   f   g   h");
        return sb.toString();
    }
}
