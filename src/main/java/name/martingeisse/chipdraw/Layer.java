package name.martingeisse.chipdraw;

import java.io.Serializable;

public final class Layer implements Serializable {

    private final int width, height;
    private final boolean[] cells;

    public Layer(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new boolean[width * height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean getCell(int x, int y) {
        return cells[getIndex(x, y)];
    }

    public void setCell(int x, int y, boolean value) {
        cells[getIndex(x, y)] = value;
    }

    private int getIndex(int x, int y) {
        if (!isValidPosition(x, y)) {
            throw new IllegalArgumentException("invalid position (" + x + ", " + y + ") for size (" + width + ", " + height + ")");
        }
        return y * width + x;
    }

    public boolean isValidPosition(int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < height);
    }

}
