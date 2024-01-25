package halloqueensgambit.java;

import halloqueensgambit.java.piece.*;

import java.util.Optional;

public class IO {
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
}
