package halloqueensgambit.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.List;
import java.util.Optional;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import halloqueensgambit.java.piece.*;

import org.junit.jupiter.api.Test;

public class SolverTesting {
    // ensures transposition table hashing is working correctly
    @Test void testZobristHashingBig(){
        Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        long zobrist = game.getZobristHash();
        Solver solver = new Solver(game, 5);
        solver.alphaBetaSearch(5, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals(zobrist, game.getZobristHash());
    }

    @Test void testZobristHashingSmall(){
        Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        long zobrist = game.getZobristHash();
        for(int i = 1; i < 4; i ++){
            Solver solver = new Solver(game, i);
            System.out.println(i);
            solver.alphaBetaSearch(i, Integer.MIN_VALUE, Integer.MAX_VALUE);
            assertEquals(zobrist, game.getZobristHash());
        }

        List<Move> allLegalMoves = game.getLegalMoves();
        for(Move m: allLegalMoves){
            Optional<Piece> captured = game.makeMove(m);
            System.out.println(m);
            game.unMakeMove(m, captured);
            assertEquals(zobrist, game.getZobristHash());
        }
    }

    // all straightforward checkmates. if fails, something is definitely wrong
    @Test void testCheckMateInOne(){
        File puzzles = new File("./src/test/java/halloqueensgambit/java/testcases/checkmate_in_one.txt");
        File answers = new File("./src/test/java/halloqueensgambit/java/testcases/checkmate_in_one_answers.txt");
        try{        
            Scanner puzzleScanner = new Scanner(puzzles);
            Scanner answerScanner = new Scanner(answers);

            long start = System.currentTimeMillis();

            for(int i = 0; i < 10; i ++){
                String fen = puzzleScanner.nextLine();
                Game game = new Game(fen);
                Solver solver = new Solver(game, 5);
                solver.alphaBetaSearch(5, Integer.MIN_VALUE, Integer.MAX_VALUE);
                String bestMove = solver.bestMove().toString();
                String answer = answerScanner.nextLine();
                System.out.println(bestMove + " " + answer);
                assertEquals(answer, bestMove);
            }
            
            long end = System.currentTimeMillis();
            System.out.println("Checkmate in one tests solved in: " + (end-start)/1000 + " seconds.");

            puzzleScanner.close();
            answerScanner.close();
        }
        catch(FileNotFoundException e){
            System.out.println("Could not find file! " + e.getLocalizedMessage());
            fail();
        }
    }

    @Test void textCheckMateInThree(){
        File puzzles = new File("./src/test/java/halloqueensgambit/java/testcases/checkmate_in_three.txt");
        File answers = new File("./src/test/java/halloqueensgambit/java/testcases/checkmate_in_three_answers.txt");
        try{        
            Scanner puzzleScanner = new Scanner(puzzles);
            Scanner answerScanner = new Scanner(answers);

            long start = System.currentTimeMillis();

            for(int i = 0; i < 10; i ++){
                String fen = puzzleScanner.nextLine();
                Game game = new Game(fen);
                Solver solver = new Solver(game, 5);
                solver.alphaBetaSearch(5, Integer.MIN_VALUE, Integer.MAX_VALUE);
                String bestMove = solver.bestMove().toString();
                String answer = answerScanner.nextLine();
                System.out.println(bestMove + " " + answer);
                assertEquals(answer, bestMove);
            }
            
            long end = System.currentTimeMillis();
            System.out.println("Checkmate in three tests solved in " + (end-start)/1000 + " seconds.");

            puzzleScanner.close();
            answerScanner.close();
        }
        catch(FileNotFoundException e){
            System.out.println("Could not find file! " + e.getLocalizedMessage());
            fail();
        }
    }

    @Test void getRandomPuzzleFromTestcases(){

    }

    @Test void fetchRandomPuzzleFromLichess(){
        try {
            URI uri = new URI("https://lichess.org/api/puzzle/W8Af8");
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine+"\n");
                response.append(inputLine);
            }
            in.close();

            System.out.println("Response from API:");
            System.out.println(response.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
