package name.martingeisse.chipdraw.pnr.cell;

public final class Port {

    private final int x, y;
    private final String name;
    private final MeaningCategory meaningCategory;

    public Port(int x, int y, String name, MeaningCategory meaningCategory) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.meaningCategory = meaningCategory;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public MeaningCategory getMeaningCategory() {
        return meaningCategory;
    }

    public enum MeaningCategory {
        VDD, GROUND, IN, OUT, OTHER
    }

}
