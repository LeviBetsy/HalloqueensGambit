package halloqueensgambit.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class SolverTesting {
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
}
