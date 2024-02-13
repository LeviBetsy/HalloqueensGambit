package halloqueensgambit.java;
import org.junit.jupiter.api.Test;

import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.piece.King;

import static halloqueensgambit.java.Side.WHITE;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTesting {
    @Test
    void testEqualsWithOnePiece() {
        Board b1 = new Board();
        b1.addToBoard(new Pos(0, 0), new King(WHITE, false));
        
        Board b2 = new Board();
        b2.addToBoard(new Pos(0, 0), new King(WHITE, false));

        assertTrue(b1.equals(b2));
    }

    @Test
    void testInitialBoard() {
        Board b1 = Board.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        Board b2 = Board.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        assertTrue(b1.equals(b2));
        assertEquals(b1, b2);
    }

    @Test
    void testInitialGame() {
        Game g1 = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        Game g2 = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        Game g3 = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        assertTrue(g1.equals(g2));
        assertFalse(g1.equals(g3));
        assertFalse(g2.equals(g3));
    }

    @Test
    void testRandomBoard() {
        String fileName = "/bestmove/a.txt";
        Game game = IO.readGameFromFile(fileName);        
        Game tmp = IO.readGameFromFile(fileName);

        assertEquals(game, tmp);
    }
}
