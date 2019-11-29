package name.martingeisse.chipdraw.pnr.design;

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

    public boolean collides(CellInstance testInstance) {
        for (CellInstance otherInstance : cellInstances) {
            if (otherInstance.overlaps(testInstance) && testInstance != otherInstance) {
                return true;
            }
        }
        return false;
    }

}
