package name.martingeisse.chipdraw.pnr.cell;

import com.google.common.collect.ImmutableList;

public final class CellTemplate {

    private final String id;
    private final int width, height;
    private final CellSymbol symbol;
    private final ImmutableList<Port> ports;

    public CellTemplate(String id, int width, int height, CellSymbol symbol, ImmutableList<Port> ports) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.symbol = symbol;
        this.ports = ports;
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

    public ImmutableList<Port> getPorts() {
        return ports;
    }

}
