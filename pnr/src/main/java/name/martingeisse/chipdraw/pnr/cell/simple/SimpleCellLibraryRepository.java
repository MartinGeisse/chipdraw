package name.martingeisse.chipdraw.pnr.cell.simple;

import name.martingeisse.chipdraw.pnr.cell.CellLibrary;
import name.martingeisse.chipdraw.pnr.cell.CellLibraryRepository;

import java.util.HashMap;

public final class SimpleCellLibraryRepository implements CellLibraryRepository {

    private final HashMap<String, CellLibrary> cellLibraries = new HashMap<>();

    public void add(CellLibrary cellLibrary) {
        cellLibraries.put(cellLibrary.getId(), cellLibrary);
    }

    @Override
    public CellLibrary getCellLibraryOrNull(String id) {
        return cellLibraries.get(id);
    }

}
