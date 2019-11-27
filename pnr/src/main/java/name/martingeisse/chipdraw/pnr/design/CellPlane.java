package name.martingeisse.chipdraw.pnr.design;

import name.martingeisse.chipdraw.pnr.util.RectangularSize;

import java.io.Serializable;

/**
 *
 */
public final class CellPlane implements Serializable, RectangularSize {

    private final int width, height;

    public CellPlane(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
