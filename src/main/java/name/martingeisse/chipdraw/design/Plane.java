package name.martingeisse.chipdraw.design;

import name.martingeisse.chipdraw.technology.PlaneSchema;
import name.martingeisse.chipdraw.util.RectangularSize;

import java.io.Serializable;
import java.util.Arrays;

/**
 * TODO consider using value types LocalMaterialIndex and GlobalMaterialIndex to avoid confusion (passing one as the other)
 */
public final class Plane implements Serializable, RectangularSize {

    public static final int EMPTY_PIXEL = 255;
    public static final int MAX_LOCAL_MATERIAL_INDEX = 250;

    private transient PlaneSchema planeSchema;
    private final int width, height;
    private final byte[] pixels;

    private Plane(PlaneSchema planeSchema, int width, int height, byte[] dataSource) {
        if (planeSchema.getMaterialNames().size() > MAX_LOCAL_MATERIAL_INDEX) {
            // so we can use bytes to store local material indices and also reserve a special byte value for EMPTY_PIXEL
            throw new IllegalArgumentException("more than 250 materials in a single plane currently not supported");
        }
        this.planeSchema = planeSchema;
        this.width = width;
        this.height = height;
        this.pixels = new byte[width * height];
        if (dataSource == null) {
            Arrays.fill(pixels, (byte) EMPTY_PIXEL);
        } else {
            System.arraycopy(dataSource, 0, pixels, 0, pixels.length);
        }
    }

    public Plane(PlaneSchema planeSchema, int width, int height) {
        this(planeSchema, width, height, null);
    }

    public Plane(Plane original) {
        this(original.getSchema(), original.getWidth(), original.getHeight(), original.pixels);
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

    public int getPixel(int x, int y) {
        return pixels[getIndex(x, y)] & 0xff;
    }

    public int getPixelAutoclip(int x, int y) {
        return isValidPosition(x, y) ? getPixel(x, y) : EMPTY_PIXEL;
    }

    public boolean isValidLocalMaterialIndex(int value) {
        return (value >= 0 && value <= MAX_LOCAL_MATERIAL_INDEX) || value == EMPTY_PIXEL;
    }

    private byte validateLocalMaterialIndex(int localMaterialIndex) {
        if (!isValidLocalMaterialIndex(localMaterialIndex)) {
            throw new IllegalArgumentException("invalid local material index: " + localMaterialIndex);
        }
        return (byte) localMaterialIndex;
    }

    public void setPixel(int x, int y, int localMaterialIndex) {
        pixels[getIndex(x, y)] = validateLocalMaterialIndex(localMaterialIndex);
    }

    public void setPixelAutoclip(int x, int y, int localMaterialIndex) {
        if (isValidPosition(x, y)) {
            setPixel(x, y, localMaterialIndex);
        }
    }

    public void drawRectangle(int x, int y, int width, int height, int localMaterialIndex) {
        validateRectangleSize(width, height);
        validatePosition(x, y);
        validatePosition(x + width - 1, y + height - 1);
        drawRectangleInternal(x, y, width, height, localMaterialIndex);
    }

    public void drawRectangleAutoclip(int x, int y, int width, int height, int localMaterialIndex) {
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
        drawRectangleInternal(x, y, width, height, localMaterialIndex);
    }

    private void validateRectangleSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("invalid rectangle size: " + width + " x " + height);
        }
    }

    private void drawRectangleInternal(int x, int y, int width, int height, int localMaterialIndex) {
        validateLocalMaterialIndex(localMaterialIndex);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                setPixel(x + i, y + j, localMaterialIndex);
            }
        }
    }

    /**
     * Note: to check uniformity without an expected value, get the value from (x, y) and pass that as expected value.
     */
    public boolean isReactangleUniform(int x, int y, int width, int height, int expectedLocalMaterialIndex) {
        validateRectangleSize(width, height);
        validatePosition(x, y);
        validatePosition(x + width - 1, y + height - 1);
        return isReactangleUniformInternal(x, y, width, height, expectedLocalMaterialIndex);
    }

    public boolean isReactangleUniformAutoclip(int x, int y, int width, int height, int expectedLocalMaterialIndex) {
        validateRectangleSize(width, height);

        // handle non-clip case
        if (isValidPosition(x, y) && isValidPosition(x + width - 1, y + height - 1)) {
            return isReactangleUniformInternal(x, y, width, height, expectedLocalMaterialIndex);
        }

        // clipped case: at least one pixel is implicitly empty, so if we are looking for nonempty pixels, it can't be uniform
        if (expectedLocalMaterialIndex != EMPTY_PIXEL) {
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
        return isReactangleUniformInternal(x, y, width, height, expectedLocalMaterialIndex);
    }

    private boolean isReactangleUniformInternal(int x, int y, int width, int height, int expectedLocalMaterialIndex) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (getPixel(x + i, y + j) != expectedLocalMaterialIndex) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isEmpty() {
        for (byte pixelValue : pixels) {
            int localMaterialIndex = pixelValue & 0xff;
            if (localMaterialIndex != EMPTY_PIXEL) {
                return false;
            }
        }
        return true;
    }

    public boolean hasMaterial(int expectedLocalMaterialIndex) {
        for (byte pixelValue : pixels) {
            int localMaterialIndex = pixelValue & 0xff;
            if (localMaterialIndex == expectedLocalMaterialIndex) {
                return true;
            }
        }
        return false;
    }

    public void copyFrom(Plane source) {
        if (source.getWidth() != getWidth() || source.getHeight() != getHeight()) {
            throw new IllegalArgumentException("source plane has different size");
        }
        copyFrom(source, 0, 0, 0, 0, getWidth(), getHeight());
    }

    public void copyFrom(Plane source, int sourceX, int sourceY, int destinationX, int destinationY, int rectangleWidth, int rectangleHeight) {
        if (source.getSchema() != getSchema()) {
            throw new IllegalArgumentException("cannot copy from plane with different schema");
        }
        source.validateSubRectangle(sourceX, sourceY, rectangleWidth, rectangleHeight);
        validateSubRectangle(destinationX, destinationY, rectangleWidth, rectangleHeight);
        for (int dx = 0; dx < rectangleWidth; dx++) {
            for (int dy = 0; dy < rectangleHeight; dy++) {
                setPixel(destinationX + dx, destinationY + dy, source.getPixel(sourceX + dx, sourceY + dy));
            }
        }
    }

}
