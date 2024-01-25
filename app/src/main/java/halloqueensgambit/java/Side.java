package halloqueensgambit.java;

public enum Side {
    BLACK(-1, "Black"),
    WHITE(1, "White");
    public final int rateMult;
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    public Side enemy(){
        if (this == BLACK){
            return WHITE;
        } else {
            return BLACK;
        }
    }

    Side(int rate, String name){
        this.rateMult = rate;
        this.name = name;
    }
}
