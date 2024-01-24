package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.stream.Collectors;
import halloqueensgambit.java.Game.Pos;

public class King implements Piece{

    private Side side;
    private Pos pos;
    private boolean hasMoved;
    public King(Side side, Pos pos, boolean hasMoved){
        this.side = side;
        this.pos = pos;
        this.hasMoved = hasMoved;
    }

    @Override
    public Pos pos() {return  this.pos;}
    @Override
    public Side side() {
        return this.side;
    }

    @Override
    public int value() {
        return 100000*side.rateMult;
    }

    @Override
    public String toString(){
        if (side == Side.WHITE)
            return " ♚ ";
        else
            return " ♔ ";
    }

    @Override
    public ArrayList<Game.Move> allLegalMove(Board board){
        ArrayList<Pos> legalPos = new ArrayList<>();
        Pos[] nextPos = {
                new Pos(this.pos.x() + 1, this.pos.y() + 1),
                new Pos(this.pos.x() + 1, this.pos.y()),
                new Pos(this.pos.x() + 1, this.pos.y() - 1),
                new Pos(this.pos.x(), this.pos.y() + 1),
                new Pos(this.pos.x(), this.pos.y() - 1),
                new Pos(this.pos.x() - 1, this.pos.y() + 1),
                new Pos(this.pos.x() - 1, this.pos.y()),
                new Pos(this.pos.x() - 1, this.pos.y() - 1)
        };

        for (Pos p : nextPos) {
            if (Game.inBound(p) && board.notAlly(p, this.side)){
                legalPos.add(p);
            }
        }

        ArrayList<Game.Move> result = legalPos.stream()
                .map(end -> new Game.Move(new King(this.side, end, true), this.pos, end)) // Modify each element as needed
                .collect(Collectors.toCollection(ArrayList::new));

        //CASTLING
        if (!this.hasMoved) {
            if (this.side == Side.WHITE) {
                var queenRook = board.lookupBoard(new Pos(1, 1)).get();
                if (queenRook instanceof Rook && queenRook.side() == Side.WHITE && !((Rook) queenRook).hasMoved &&
                        board.lookupBoard(new Pos(2, 1)).isEmpty()
                        && board.lookupBoard(new Pos(3, 1)).isEmpty()
                        && board.lookupBoard(new Pos(4, 1)).isEmpty()) {
                    result.add(new Game.Move(new King(this.side, new Pos(3, 1), true), this.pos, new Pos(3, 1)));
                }
                var kingRook = board.lookupBoard(new Pos(8, 1)).get();
                if (kingRook instanceof Rook && queenRook.side() == Side.WHITE && !((Rook) kingRook).hasMoved &&
                        board.lookupBoard(new Pos(6, 1)).isEmpty()
                        && board.lookupBoard(new Pos(7, 1)).isEmpty()) {
                    result.add(new Game.Move(new King(this.side, new Pos(7, 1), true), this.pos, new Pos(7, 1)));
                }
            } else {
                var queenRook = board.lookupBoard(new Pos(1, 8)).get();
                if (queenRook instanceof Rook && queenRook.side() == Side.WHITE && !((Rook) queenRook).hasMoved &&
                        board.lookupBoard(new Pos(2, 8)).isEmpty()
                        && board.lookupBoard(new Pos(3, 8)).isEmpty()
                        && board.lookupBoard(new Pos(4, 8)).isEmpty()) {
                    result.add(new Game.Move(new King(this.side, new Pos(3, 8), true), this.pos, new Pos(3, 8)));
                }
                var kingRook = board.lookupBoard(new Pos(8, 8)).get();
                if (kingRook instanceof Rook && queenRook.side() == Side.WHITE && !((Rook) kingRook).hasMoved &&
                        board.lookupBoard(new Pos(6, 8)).isEmpty()
                        && board.lookupBoard(new Pos(7, 8)).isEmpty()) {
                    result.add(new Game.Move(new King(this.side, new Pos(7, 8), true), this.pos, new Pos(7, 8)));
                }
            }
        }

        return result;
    }
}
