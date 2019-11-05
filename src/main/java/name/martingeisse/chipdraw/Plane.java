package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.technology.PlaneSchema;

import java.io.Serializable;
import java.util.Arrays;

/**
 * TODO rename "cells" to "pixels"? The former already has a meaning in chip design.
 *
 * TODO the remaining code uses the term "layers" but this code says "values"
 */
public final class Plane implements Serializable {

    public static final int EMPTY_CELL = 255;
    public static final int MAX_CELL_VALUE = 250;

    private transient PlaneSchema planeSchema;
    private final int width, height;
    private final byte[] cells;

    private Plane(PlaneSchema planeSchema, int width, int height, byte[] dataSource) {
        if (planeSchema.getLayerNames().size() > MAX_CELL_VALUE) {
            // so we can use bytes to store cell values and also reserver a special byte value for EMPTY_CELL
            throw new IllegalArgumentException("more than 250 layers in a single plane currently not supported");
        }
        this.planeSchema = planeSchema;
        this.width = width;
        this.height = height;
        this.cells = new byte[width * height];
        if (dataSource == null) {
            Arrays.fill(cells, (byte)EMPTY_CELL);
        } else {
            System.arraycopy(dataSource, 0, cells, 0, cells.length);
        }
    }

    public Plane(PlaneSchema planeSchema, int width, int height) {
        this(planeSchema, width, height, null);
    }

    public Plane(Plane original) {
        this(original.getSchema(), original.getWidth(), original.getHeight(), original.cells);
    }

    void initializeAfterDeserialization(PlaneSchema planeSchema) {
		this.planeSchema = planeSchema;
    }

    public PlaneSchema getSchema() {
        return planeSchema;
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

    public int getCell(int x, int y) {
        return cells[getIndex(x, y)] & 0xff;
    }

    public int getCellAutoclip(int x, int y) {
        return isValidPosition(x, y) ? getCell(x, y) : EMPTY_CELL;
    }

    public boolean isValidCellValue(int value) {
        return (value >= 0 && value <= MAX_CELL_VALUE) || value == EMPTY_CELL;
    }

    private byte validateCellValue(int value) {
        if (!isValidCellValue(value)) {
            throw new IllegalArgumentException("invalid cell value: " + value);
        }
        return (byte)value;
    }

    public void setCell(int x, int y, int value) {
        cells[getIndex(x, y)] = validateCellValue(value);
    }

    public void setCellAutoclip(int x, int y, int value) {
        if (isValidPosition(x, y)) {
            setCell(x, y, value);
        }
    }

    public void drawRectangle(int x, int y, int width, int height, int value) {
        validateRectangleSize(width, height);
        validatePosition(x, y);
        validatePosition(x + width - 1, y + height - 1);
        drawRectangleInternal(x, y, width, height, value);
    }

    public void drawRectangleAutoclip(int x, int y, int width, int height, int value) {
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

    private void drawRectangleInternal(int x, int y, int width, int height, int value) {
        validateCellValue(value);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                setCell(x + i, y + j, value);
            }
        }
    }

    /**
     * Note: to check uniformity without an expected value, get the value from (x, y) and pass that as expected value.
     */
    public boolean isReactangleUniform(int x, int y, int width, int height, int expectedValue) {
        validateRectangleSize(width, height);
        validatePosition(x, y);
        validatePosition(x + width - 1, y + height - 1);
        return isReactangleUniformInternal(x, y, width, height, expectedValue);
    }

    public boolean isReactangleUniformAutoclip(int x, int y, int width, int height, int expectedValue) {
        validateRectangleSize(width, height);

        // handle non-clip case
        if (isValidPosition(x, y) && isValidPosition(x + width - 1, y + height - 1)) {
            return isReactangleUniformInternal(x, y, width, height, expectedValue);
        }

        // clipped case: at least one pixel is implicitly empty, so if we are looking for nonempty pixels, it can't be uniform
        if (expectedValue != EMPTY_CELL) {
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
        return isReactangleUniformInternal(x, y, width, height, expectedValue);
    }

    private boolean isReactangleUniformInternal(int x, int y, int width, int height, int expectedValue) {
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
