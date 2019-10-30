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

    public Layer createCopy() {
        Layer copy = new Layer(width, height);
        System.arraycopy(cells, 0, copy.cells, 0, cells.length);
        return copy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isValidPosition(int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < height);
    }

    private void validatePosition(int x, int y) {
        if (!isValidPosition(x, y)) {
            throw new IllegalArgumentException("invalid position (" + x + ", " + y + ") for size (" + width + ", " + height + ")");
        }
    }

    private int getIndex(int x, int y) {
        validatePosition(x, y);
        return y * width + x;
    }

    public boolean getCell(int x, int y) {
        return cells[getIndex(x, y)];
    }

    public boolean getCellAutoclip(int x, int y) {
        return isValidPosition(x, y) && getCell(x, y);
    }

    public void setCell(int x, int y, boolean value) {
        cells[getIndex(x, y)] = value;
    }

    public void setCellAutoclip(int x, int y, boolean value) {
        if (isValidPosition(x, y)) {
            setCell(x, y, value);
        }
    }

    public void drawRectangle(int x, int y, int width, int height, boolean value) {
        validateRectangleSize(width, height);
        validatePosition(x, y);
        validatePosition(x + width - 1, y + height - 1);
        drawRectangleInternal(x, y, width, height, value);
    }

    public void drawRectangleAutoclip(int x, int y, int width, int height, boolean value) {
        validateRectangleSize(width, height);
        if (x < 0) {
            width += x;
            x = 0;
        }
        if (y < 0) {
            height += y;
            y = 0;
        }
        if (width > this.width - x) {
            width = this.width - x;
        }
        if (height > this.height - y) {
            height = this.height - y;
        }
        drawRectangleInternal(x, y, width, height, value);
    }

    private void validateRectangleSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("invalid rectangle size: " + width + " x " + height);
        }
    }

    private void drawRectangleInternal(int x, int y, int width, int height, boolean value) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                setCell(x + i, y + j, value);
            }
        }
    }

}
