/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package halloqueensgambit.java;

import java.io.IOException;
import java.util.Scanner;

public class App {
    public String getGreeting() {
        return "a";
    }

    public void printNextGames(Game game) {
        var lst = game.allNextGames();
        System.out.println("ALL NEXT GAME:");
        for (var g : lst) {
            System.out.println();
            System.out.println(g);
        }
    }

    public void twoPlayer(Game game) {
        Game newGame = game;
        // TODO: this is lazy
        while (true) {
            System.out.println(newGame);
            newGame = IO.playerMove(newGame, new Scanner(System.in));
        }
    }

    public static void main(String[] args) throws IOException {
        String fileName;
        if (args.length == 0) {
            fileName = "/bestmove/a.txt";
        } else {
            fileName = args[0];
        }
        Game game = IO.readGameFromFile(fileName);

        System.out.println(game);
        Solver solver = new Solver(game);
        long start = System.currentTimeMillis();
        int eval = solver.solve(4);
        System.out.println("Mutable solver: ");
        System.out.println(eval);
        long end = System.currentTimeMillis();
        System.out.println("Time elapsed: " +  (end-start));
        double efficiency = Solver.numPositionsSeen/(end-start);
        System.out.println("Average positions/second: " + efficiency);
    }
}
