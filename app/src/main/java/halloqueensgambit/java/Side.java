package halloqueensgambit.java;

public enum Side {
    BLACK(-1),
    WHITE(1);
    public final int rateMult;

    Side(int rate){
        this.rateMult = rate;
    }
}
