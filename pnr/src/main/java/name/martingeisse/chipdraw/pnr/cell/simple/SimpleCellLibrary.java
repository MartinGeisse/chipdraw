package name.martingeisse.chipdraw.pnr.cell.simple;

import name.martingeisse.chipdraw.pnr.cell.Cell;
import name.martingeisse.chipdraw.pnr.cell.CellLibrary;

import java.util.HashMap;

public final class SimpleCellLibrary implements CellLibrary {

    private final String id;
    private final HashMap<String, Cell> cells = new HashMap<>();

    public SimpleCellLibrary(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void add(Cell cell) {
        cells.put(cell.getId(), cell);
    }

    @Override
    public Cell getCellOrNull(String id) {
        return cells.get(id);
    }

}
