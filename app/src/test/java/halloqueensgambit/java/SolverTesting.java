package halloqueensgambit.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import halloqueensgambit.java.piece.*;

import org.junit.jupiter.api.Test;

public class SolverTesting {
    // ensures indexing is working properly heheheheh
    @Test void testZobristIndexing(){
        ArrayList<Piece> pieces = new ArrayList<Piece>();
        pieces.add(new Pawn(Side.WHITE));
        pieces.add(new Knight(Side.WHITE));
        pieces.add(new Bishop(Side.WHITE));
        pieces.add(new Rook(Side.WHITE, false));
        pieces.add(new Queen(Side.WHITE));
        pieces.add(new King(Side.WHITE, false));

        pieces.add(new Pawn(Side.BLACK));
        pieces.add(new Knight(Side.BLACK));
        pieces.add(new Bishop(Side.BLACK));
        pieces.add(new Rook(Side.BLACK, false));
        pieces.add(new Queen(Side.BLACK));
        pieces.add(new King(Side.BLACK, false));

        ArrayList<Game.Pos> positions = new ArrayList<Game.Pos>();
        for(int x = 1; x < 9; ++x){
            for(int y = 1; y < 9; ++y){
                positions.add(new Game.Pos(x, y));
            }
        }

        for(Piece piece: pieces){
            for(Game.Pos pos: positions){
                System.out.println("Piece: " + piece + ", Position: " + pos + ", Index: " + Game.getZobristIndex(pos, piece));
            }
        }
    }

    @Test void testZobristHashingSmall(){
        Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        long zobrist = game.getZobristHash();
        Solver solver = new Solver(game);
        solver.alphaBetaSearch(3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals(zobrist, game.getZobristHash());
    }

    @Test void testZobristHashingBig(){
        HashMap<Long, String> fens = new HashMap<Long, String>();
        Game game = new Game("r3k2r/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/R3K2R w KQkq - 0 1");

        List<Move> allLegalMoves1 = game.getLegalMoves();
        for(Move m1: allLegalMoves1){
            Optional<Piece> captured1 = game.makeMove(m1);
            fens.put(game.generateZobristHash(), game.board().toFEN());
            
            System.out.println(m1);

            
            List<Move> allLegalMoves2 = game.getLegalMoves();
            for(Move m2: allLegalMoves2){
                Optional<Piece> captured2 = game.makeMove(m2);
                fens.put(game.generateZobristHash(), game.board().toFEN());
                
                System.out.println("\t"+m2);
                
                List<Move> allLegalMoves3 = game.getLegalMoves();
                for(Move m3: allLegalMoves3){
                    Optional<Piece> captured3 = game.makeMove(m3);
                    fens.put(game.generateZobristHash(), game.board().toFEN());
                    
                    System.out.println("\t\t"+m3);

                    assertEquals(game.generateZobristHash(), game.getZobristHash());
                    game.unMakeMove(m3, captured3);
                    assertEquals(game.generateZobristHash(), game.getZobristHash());
                }
                
                assertEquals(game.generateZobristHash(), game.getZobristHash());
                game.unMakeMove(m2, captured2);
                assertEquals(game.generateZobristHash(), game.getZobristHash());
            }

            assertEquals(game.generateZobristHash(), game.getZobristHash());
            game.unMakeMove(m1, captured1);
            assertEquals(game.generateZobristHash(), game.getZobristHash());
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
                Solver solver = new Solver(game);
                Solver.MoveRating MR = solver.alphaBetaSearch(5, Integer.MIN_VALUE, Integer.MAX_VALUE);
                String bestMove = MR.move().toString();
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
                Solver solver = new Solver(game);
                Solver.MoveRating MR = solver.alphaBetaSearch(5, Integer.MIN_VALUE, Integer.MAX_VALUE);
                String bestMove = MR.move().toString();
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

    @Test void testLichessPuzzles(){
        int currentPuzzleNumber = 1;
        int numPuzzles = 1;
        int totalRating = 0;
        int totalRatingIncorrect = 0;
        int numCorrect = 0;
        int numIncorrect = 0;
        int highestPuzzleCorrect = 0;
        int lowestPuzzleIncorrect = 3000;

        File lichessPuzzles = new File("./src/test/java/halloqueensgambit/java/testcases/lichess_puzzles_small.csv");
        String failedPuzzleFile = "./src/test/java/halloqueensgambit/java/testcases/failed_puzzles.csv";
        
        try{
            BufferedWriter failedPuzzleWriter = new BufferedWriter(new FileWriter(failedPuzzleFile));
            Scanner puzzleScanner = new Scanner(lichessPuzzles);
            failedPuzzleWriter.write("puzzleid,rating");
            failedPuzzleWriter.newLine();
            puzzleScanner.nextLine(); // header row
            long start = System.currentTimeMillis();

            int currentStrength = 1400;
            int kfactor = 500; // high for big provisional boosts at the start, decreases over time

            for(int i = 0; i < numPuzzles; i ++){
                // skip a random number of lines (0 < skip < 50)
                // for(int skip = 0; skip < (int) (Math.random() * 50); ++skip){
                //     puzzleScanner.nextLine();
                // }

                // get a puzzle
                String[] line = puzzleScanner.nextLine().split(",");
                String[] moves = line[2].split(" ");
                Game game = new Game(line[1]);
                int puzzleRating = Integer.parseInt(line[3]);
                totalRating += puzzleRating;
                
                // make the first move (the "mistake") of the puzzle
                game.makeMove(IO.scanMove(moves[0]));

                // get the best move
                Solver solver = new Solver(game);
                Solver.MoveRating MR = solver.alphaBetaSearch(6, Integer.MIN_VALUE, Integer.MAX_VALUE);
                String bestMove = MR.move().toString();
                
                // get results, calculate new rating and output results to debug
                System.out.print(currentPuzzleNumber + " " + line[0] + " (" + puzzleRating + ")"); // puzzle id + rating
                currentPuzzleNumber ++;

                if(bestMove.equals(moves[1])){
                    numCorrect ++;
                    highestPuzzleCorrect = Math.max(puzzleRating, highestPuzzleCorrect);
                    currentStrength += kfactor * (1 - 1/(1 + Math.pow(10, (puzzleRating - currentStrength)/400.0)));
                    kfactor = Math.max(32, (int) (kfactor * 0.80));
                    System.out.println(" Solved. New rating: " + currentStrength);
                }else{
                    numIncorrect ++;
                    totalRatingIncorrect += puzzleRating;
                    lowestPuzzleIncorrect = Math.min(puzzleRating, lowestPuzzleIncorrect);
                    currentStrength += kfactor * (0 - 1/(1 + Math.pow(10, (puzzleRating - currentStrength)/400.0)));
                    kfactor = Math.max(16, (int) (kfactor * 0.80));
                    System.out.println(" Failed to solve. New rating: " + currentStrength);
                    failedPuzzleWriter.write(line[0] + "," + puzzleRating);
                    failedPuzzleWriter.newLine();
                }
            }
            
            // calculate time
            long end = System.currentTimeMillis();
            System.out.println("\n---------------- RESULTS ----------------");
            System.out.println(numPuzzles + " puzzles attempted (" + numCorrect + " correct, " + numIncorrect + " incorrect)");
            System.out.println("Average puzzle rating: " + totalRating/numPuzzles);
            System.out.println("Average incorrect puzzle rating: " + (totalRatingIncorrect)/numIncorrect);
            System.out.println("Highest correct puzzle: " + highestPuzzleCorrect);
            System.out.println("Lowest incorrect puzzle: " + lowestPuzzleIncorrect);
            System.out.println("\nAverage time per puzzle: " + (end-start)/numPuzzles + " milliseconds");
            System.out.println("Estimated strength: " + currentStrength);
            System.out.println("\nFailed puzzle IDs can be located in " + failedPuzzleFile);

            puzzleScanner.close();
            failedPuzzleWriter.close();
        }
        catch(FileNotFoundException e){
            System.out.println("Could not find file! " + e.getLocalizedMessage());
            fail();
        }
        catch(IOException e){
            System.out.println("Failed to write to file! " + e.getLocalizedMessage());
        }

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
