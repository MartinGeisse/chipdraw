package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.technology.PlaneSchema;

import java.io.Serializable;

/**
 * TODO rename "cells" to "pixels"? The former already has a meaning in chip design.
 */
public final class Layer implements Serializable {

    private transient PlaneSchema layerSchema;
    private final int width, height;
    private final boolean[] cells;

    public Layer(PlaneSchema layerSchema, int width, int height) {
        this.layerSchema = layerSchema;
        this.width = width;
        this.height = height;
        this.cells = new boolean[width * height];
    }

    void initializeAfterDeserialization(PlaneSchema layerSchema) {
		this.layerSchema = layerSchema;
    }

    public Layer createCopy() {
        Layer copy = new Layer(layerSchema, width, height);
        System.arraycopy(cells, 0, copy.cells, 0, cells.length);
        return copy;
    }

    public PlaneSchema getLayerSchema() {
        return layerSchema;
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

    /**
     * Note: to check uniformity without an expected value, get the value from (x, y) and pass that as expected value.
     */
    public boolean isReactangleUniform(int x, int y, int width, int height, boolean expectedValue) {
        validateRectangleSize(width, height);
        validatePosition(x, y);
        validatePosition(x + width - 1, y + height - 1);
        return isReactangleUniformInternal(x, y, width, height, expectedValue);
    }

    public boolean isReactangleUniformAutoclip(int x, int y, int width, int height, boolean expectedValue) {
        validateRectangleSize(width, height);

        // handle non-clip case
        if (isValidPosition(x, y) && isValidPosition(x + width - 1, y + height - 1)) {
            return isReactangleUniformInternal(x, y, width, height, expectedValue);
        }

        // clipped case: at least one pixel is implicitly empty, so we can't be uniformly filled
        if (expectedValue) {
            return false;
        }

        // check if uniformly empty
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
        return isReactangleUniformInternal(x, y, width, height, false);
    }

    private boolean isReactangleUniformInternal(int x, int y, int width, int height, boolean expectedValue) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (getCell(x + i, y + j) != expectedValue) {
                    return false;
                }
            }
        }
        return true;
    }

}
