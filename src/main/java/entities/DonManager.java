package entities;

public class DonManager {
    private static DonManager instance;
    private Don don;

    private DonManager() {
        // Private constructor to prevent instantiation
    }

    public static DonManager getInstance() {
        if (instance == null) {
            instance = new DonManager();
        }
        return instance;
    }

    public Don getDon() {
        return don;
    }

    public void setDon(Don don) {
        this.don = don;
    }
}
