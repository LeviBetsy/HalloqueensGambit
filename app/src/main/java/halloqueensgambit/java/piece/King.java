package halloqueensgambit.java.piece;

import halloqueensgambit.java.Board;
import halloqueensgambit.java.Game;
import halloqueensgambit.java.Side;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import halloqueensgambit.java.Game.Pos;
import halloqueensgambit.java.Move;


public class King implements Piece{
    private final Side side;
    public boolean hasMoved;
    public King(Side side, boolean hasMoved){
        this.side = side;
        this.hasMoved = hasMoved;
    }

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
    public ArrayList<Move> allLegalMove(Pos pos, Board board){
        ArrayList<Pos> legalPos = new ArrayList<>();
        Pos[] nextPos = {
                new Pos(pos.x() + 1, pos.y() + 1),
                new Pos(pos.x() + 1, pos.y()),
                new Pos(pos.x() + 1, pos.y() - 1),
                new Pos(pos.x(), pos.y() + 1),
                new Pos(pos.x(), pos.y() - 1),
                new Pos(pos.x() - 1, pos.y() + 1),
                new Pos(pos.x() - 1, pos.y()),
                new Pos(pos.x() - 1, pos.y() - 1)
        };

        for (Pos p : nextPos) {
            if (Game.inBound(p) && board.notAlly(p, this.side)){
                legalPos.add(p);
            }
        }

        ArrayList<Move> result = legalPos.stream()
                .map(end -> new Move(pos, end))
                .collect(Collectors.toCollection(ArrayList::new));

        //CASTLING
        if (!this.hasMoved) {
            if (this.side == Side.WHITE) {
                var queenRookSq = board.lookup(new Pos(1, 1));
                if (queenRookSq.isPresent()) {
                    var queenRook = queenRookSq.get();
                    if (queenRook instanceof Rook && queenRook.side() == Side.WHITE && !((Rook) queenRook).hasMoved &&
                            isAllBlank(Arrays.asList(new Pos(2, 1), new Pos(3, 1), new Pos(4, 1)), board)) {
                        result.add(new Move(pos, new Pos(3, 1)));
                    }
                }
                var kingRookSq = board.lookup(new Pos(8, 1));
                if (kingRookSq.isPresent()) {
                    var kingRook = kingRookSq.get();
                    if (kingRook instanceof Rook && kingRook.side() == Side.WHITE && !((Rook) kingRook).hasMoved &&
                            isAllBlank(Arrays.asList(new Pos(6, 1), new Pos(7, 1)), board)) {
                        result.add(new Move(pos, new Pos(7, 1)));
                    }
                }
            } else {
                var queenRookSq = board.lookup(new Pos(1, 8));
                if (queenRookSq.isPresent()) {
                    var queenRook = queenRookSq.get();
                    if (queenRook instanceof Rook && queenRook.side() == Side.BLACK && !((Rook) queenRook).hasMoved &&
                            isAllBlank(Arrays.asList(new Pos(2, 8), new Pos(3, 8), new Pos(4, 8)), board)) {
                        result.add(new Move(pos, new Pos(3, 8)));
                    }
                }
                var kingRookSq = board.lookup(new Pos(8, 8));
                if (kingRookSq.isPresent()) {
                    var kingRook = kingRookSq.get();
                    if (kingRook instanceof Rook && kingRook.side() == Side.BLACK && !((Rook) kingRook).hasMoved &&
                            isAllBlank(Arrays.asList(new Pos(6, 8), new Pos(7, 8)), board)) {
                        result.add(new Move(pos, new Pos(7, 8)));
                    }
                }
            }
        }
        return result;
    }

    private boolean isAllBlank(List<Pos> squares, Board board){
        for (Pos pos : squares){
            if (board.lookup(pos).isPresent())
                return false;
        }
        return true;
    }
}
