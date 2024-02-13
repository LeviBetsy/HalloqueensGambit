package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;
import halloqueensgambit.java.Game.Pos;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class IO {
    public static Optional<Piece> scanPiece(String c){
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

    public static Move scanMove(String str){
        Pos startPos = new Pos((str.charAt(0) - 'a' + 1), (str.charAt(1) - '1' + 1));
        Pos endPos = new Pos((str.charAt(3) - 'a' + 1), (str.charAt(4) - '1' + 1));
        return new Move(startPos, endPos);
    }

    public static Game playerMove(Game game, Scanner scanner){
        ArrayList<Move> gameLegalMoves = game.getLegalMoves();

        Move userMove;
        while (true) {
            String userInput = scanner.nextLine();
            userMove = scanMove(userInput);
            if (!gameLegalMoves.contains(userMove)){
                System.out.println("Move is not valid, please retry");
            } else {
                break;
            }
        }
        game.makeMove(userMove);
        return game;
        //scanner.close(
    }

    //constructor for creating game from file name
    //file must be inside of games folder
    public static Game readGameFromFile(String fileName){
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        try{
            // Resolve the file path
            String currentDirectory = System.getProperty("user.dir");
            // Connect the filepath
            Path filePath = Paths.get(currentDirectory, "src/main/java/halloqueensgambit/java/games", fileName);
            Scanner scanner = new Scanner(filePath);

            fen = scanner.nextLine();
            
            scanner.close();
        } catch(Exception e){
            System.out.println("Unable to read board: " + e.getLocalizedMessage());
        }
        return new Game(fen);
    }
}