/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package halloqueensgambit.java;
import halloqueensgambit.java.Game.*;
import halloqueensgambit.java.piece.*;

import java.util.Optional;

import static halloqueensgambit.java.Side.WHITE;
import static halloqueensgambit.java.Side.BLACK;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

//    public void initialBoard() {
//        TreeMap<Pos, Piece>  initialBoardTree = new TreeMap<>();
//        initialBoardTree.put(new Pos(1,1), new Rook(WHITE));
//        initialBoardTree.put(new Pos(2,1), new Knight(WHITE));
//        Board board = new Board(initialBoardTree);
//        System.out.println(board.toString());
//    }
//
//    public void initialGame() {
//        TreeMap<Pos, Piece>  initialBoardTree = new TreeMap<>();
//        initialBoardTree.put(new Pos(1,1), new Rook(WHITE));
//        initialBoardTree.put(new Pos(2,1), new Knight(WHITE));
//        Board board = new Board(initialBoardTree);
//        Game g = new Game(WHITE, board, 5);
//        System.out.println(g.toString());
//    }

    public static void main(String[] args) throws IOException {
//        System.out.println(new App().getGreeting());

        String fileName;
        if (args.length == 0){
            fileName = "initialGame.txt";
        } else {
            fileName = args[0];
        }
        Game game = scanGame(fileName);
        System.out.println(game);
    }

    public static Game scanGame(String fileName) throws IOException {
        // Resolve the file path

        String currentDirectory = System.getProperty("user.dir");
        // Connect the filepath
        Path filePath = Paths.get(currentDirectory, "src/main/java/halloqueensgambit/java/games", fileName);
        Scanner scanner = new Scanner(filePath);


        int turn = Integer.parseInt(scanner.nextLine());
        Side side = (scanner.nextLine().equals("B")) ? BLACK : WHITE;
        Board b = new Board();

        for (int y = 8; y >= 1; y--){
            String row = scanner.nextLine();
            //TODO: maybe throw error for index out of bound
            //splitting a row into individual squares
            String[] squares = row.split("\\s+");
            for (int x = 1; x <= 8; x++){
                Optional<Piece> currentPiece = scanPiece(squares[x - 1]);
                //if scan Piece does not return an Optional value
                if (currentPiece.isPresent()){
                    b.addToBoard(new Pos(x,y), currentPiece.get());
                }
            }
        }
        return new Game(side, b, turn);
    }

    private static Optional<Piece> scanPiece(String c){
        return switch (c) {
            case "R" -> Optional.of(new Rook(WHITE));
            case "r" -> Optional.of(new Rook(BLACK));
            case "N" -> Optional.of(new Knight(WHITE));
            case "n" -> Optional.of(new Knight(BLACK));
            case "B" -> Optional.of(new Bishop(WHITE));
            case "b" -> Optional.of(new Bishop(BLACK));
            case "K" -> Optional.of(new King(WHITE));
            case "k" -> Optional.of(new King(BLACK));
            case "Q" -> Optional.of(new Queen(WHITE));
            case "q" -> Optional.of(new Queen(BLACK));
            case "P" -> Optional.of(new Pawn(WHITE));
            case "p" -> Optional.of(new Pawn(BLACK));
            default -> Optional.empty();
        };
    }
}
