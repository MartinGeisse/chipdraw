package name.martingeisse.chipdraw.pnr.design;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pnr.util.RectangularSize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class CellPlane implements Serializable, RectangularSize {

    private final int width, height;
    private final List<CellInstance> cellInstances;

    public CellPlane(int width, int height) {
        this.width = width;
        this.height = height;
        this.cellInstances = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void add(CellInstance cellInstance) {
        cellInstances.add(cellInstance);
    }

    public void remove(CellInstance cellInstance) {
        cellInstances.remove(cellInstance);
    }

    public Iterable<CellInstance> getCellInstances() {
        return cellInstances;
    }

    public ImmutableList<CellInstance> getCellInstancesAsList() {
        return ImmutableList.copyOf(cellInstances);
    }

    public boolean collides(CellInstance testInstance) {
        for (CellInstance otherInstance : cellInstances) {
            if (otherInstance.overlaps(testInstance) && testInstance != otherInstance) {
                return true;
            }
        }
        return false;
    }

    public CellInstance findInstanceForPosition(int x, int y) {
        for (CellInstance instance : cellInstances) {
            if (instance.overlaps(x, y)) {
                return instance;
            }
        }
        return null;
    }

    public CellInstance findAndRemoveInstanceForPosition(int x, int y) {
        CellInstance instance = findInstanceForPosition(x, y);
        if (instance != null) {
            remove(instance);
        }
        return instance;
    }

}
