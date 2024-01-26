package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;
import halloqueensgambit.java.Game.Move;
import halloqueensgambit.java.Game.Pos;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

public class IO {
    private HashMap<Character, Integer> matchLetter;
    private HashMap<Character, Integer> matchNumber;
    private Scanner scanner;
    public IO(){
        this.matchLetter = new HashMap<>();
        this.matchLetter.put('a', 1);
        this.matchLetter.put('b', 2);
        this.matchLetter.put('c', 3);
        this.matchLetter.put('d', 4);
        this.matchLetter.put('e', 5);
        this.matchLetter.put('f', 6);
        this.matchLetter.put('g', 7);
        this.matchLetter.put('h', 8);
        this.matchNumber = new HashMap<>();
        this.matchNumber.put('1', 1);
        this.matchNumber.put('2', 2);
        this.matchNumber.put('3', 3);
        this.matchNumber.put('4', 4);
        this.matchNumber.put('5', 5);
        this.matchNumber.put('6', 6);
        this.matchNumber.put('7', 7);
        this.matchNumber.put('8', 8);
        this.scanner = new Scanner(System.in);
    }
    public static Optional<Piece> scanPiece(String c, Game.Pos pos){
        return switch (c) {
            case "R" -> Optional.of(new Rook(Side.WHITE,false));
            case "r" -> Optional.of(new Rook(Side.BLACK,false));
            case "N" -> Optional.of(new Knight(Side.WHITE));
            case "n" -> Optional.of(new Knight(Side.BLACK));
            case "B" -> Optional.of(new Bishop(Side.WHITE));
            case "b" -> Optional.of(new Bishop(Side.BLACK));
            case "K" -> Optional.of(new King(Side.WHITE,false));
            case "k" -> Optional.of(new King(Side.BLACK, false));
            case "Q" -> Optional.of(new Queen(Side.WHITE));
            case "q" -> Optional.of(new Queen(Side.BLACK));
            case "P" -> Optional.of(new Pawn(Side.WHITE));
            case "p" -> Optional.of(new Pawn(Side.BLACK));
            default -> Optional.empty();
        };
    }

    public Optional<Move> scanMove(String str){
        String[] positions = str.split(" ");
        char[] start = positions[0].toCharArray();
        Integer startx = this.matchLetter.get(start[0]);
        Integer starty = this.matchNumber.get(start[1]);
        if (startx == null || starty == null){
            return Optional.empty();
        }
        char[] end = positions[1].toCharArray();
        Integer endx = this.matchLetter.get(end[0]);
        Integer endy = this.matchNumber.get(end[1]);
        if (endx == null || endy == null){
            return Optional.empty();
        }

        return Optional.of(new Move(new Pos(startx, starty), new Pos(endx, endy)));
    }

    public Game playerMove(Game game){
        ArrayList<Move> gameLegalMoves = game.getLegalMoves();

        Optional<Move> userMove = Optional.empty();
        while (userMove.isEmpty()) {
            System.out.println("Enter your move (in format a2 a3):");
            userMove = scanMove(scanner.nextLine());
            if (userMove.isEmpty() || !gameLegalMoves.contains(userMove.get())){
                userMove = Optional.empty();
                System.out.println("Move is not valid, please retry");
            }
        }

        // Close the scanner to release resources
        return game.makeMove(userMove.get());
    }
}
