package name.martingeisse.chipdraw.pnr.cell.simple;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pnr.cell.CellTemplate;
import name.martingeisse.chipdraw.pnr.cell.CellLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SimpleCellLibrary implements CellLibrary {

    private final String id;
    private final HashMap<String, CellTemplate> cellTemplates = new HashMap<>();

    public SimpleCellLibrary(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void add(CellTemplate cell) {
        cellTemplates.put(cell.getId(), cell);
    }

    @Override
    public ImmutableList<String> getAllIds() {
        List<String> ids = new ArrayList<>(cellTemplates.keySet());
        ids.sort(null);
        return ImmutableList.copyOf(ids);
    }

    @Override
    public CellTemplate getCellTemplateOrNull(String id) {
        return cellTemplates.get(id);
    }

}
