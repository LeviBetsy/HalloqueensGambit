package halloqueensgambit.java;

import halloqueensgambit.java.Game.Pos;

public class Move{
    public Pos start;
    public Pos end;
    public boolean isPromotion;

    public Move(Pos start, Pos end){
        this.start = start;
        this.end = end;
        this.isPromotion = false;
    }

    public Move(Pos start, Pos end, boolean promotion){
        this.start = start;
        this.end = end;
        this.isPromotion = promotion;
    }

    // used primarily to check if the king castled
    public int getDistance(){
        int horizontal = Math.abs(end.x() - start.x());
        int vertical = Math.abs(end.y() - start.y());
        return Math.max(horizontal, vertical);
    }

    @Override
    public String toString() {
        return this.start.toString() + this.end.toString();
    }
}