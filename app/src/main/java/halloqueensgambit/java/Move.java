package halloqueensgambit.java;

import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.piece.*;

import java.util.Optional;

public class Move{
    Pos start;
    Pos end;
    boolean isPromotion;
    Optional<Piece> pieceCaptured;

    public Move(Pos start, Pos end){
        this.start = start;
        this.end = end;
        this.isPromotion = false;
        this.pieceCaptured = Optional.empty();
    }

    public Move(Pos start, Pos end, Piece captured){
        this.start = start;
        this.end = end;
        this.isPromotion = false;
        this.pieceCaptured = Optional.of(captured);
    }

    public Move(Pos start, Pos end, Piece captured, boolean promotion){
        this.start = start;
        this.end = end;
        this.isPromotion = promotion;
        this.pieceCaptured = Optional.of(captured);
    }

    // used primarily to check if the king castled
    public int getDistance(){
        int horizontal = Math.abs(end.x() - start.x());
        int vertical = Math.abs(end.y() - start.y());
        return Math.min(horizontal, vertical);
    }
}