package name.martingeisse.chipdraw.pnr.cell;

public final class CellTemplate {

    private final String id;
    private final int width, height;
    private final CellSymbol symbol;

    public CellTemplate(String id, int width, int height, CellSymbol symbol) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.symbol = symbol;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public CellSymbol getSymbol() {
        return symbol;
    }

}
