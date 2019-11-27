package name.martingeisse.chipdraw.pnr.cell;

public final class CellTemplate {

    private final String id;
    private final CellSymbol symbol;

    public CellTemplate(String id, CellSymbol symbol) {
        this.id = id;
        this.symbol = symbol;
    }

    public String getId() {
        return id;
    }

    public CellSymbol getSymbol() {
        return symbol;
    }

}
