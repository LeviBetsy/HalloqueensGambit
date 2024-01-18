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


    Side(int rate, String name){
        this.rateMult = rate;
        this.name = name;
    }
}
