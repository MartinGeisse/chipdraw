package name.martingeisse.chipdraw.design;

import name.martingeisse.chipdraw.util.RectangularSize;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 */
public final class Plane implements Serializable, RectangularSize {

    private static final long serialVersionUID = 1;

    private static final int EMPTY_PIXEL = 255;
    private static final int MAX_LOCAL_MATERIAL_INDEX = 250;

    private transient PlaneSchema planeSchema;
    private final int width, height;
    private final byte[] pixels;

    private Plane(PlaneSchema planeSchema, int width, int height, byte[] dataSource) {
        if (planeSchema.getMaterials().size() > MAX_LOCAL_MATERIAL_INDEX) {
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

    public boolean isMaterialValid(Material material) {
        return material.getPlaneSchema() == planeSchema;
    }

    public void validateMaterial(Material material) {
        if (!isMaterialValid(material)) {
            throw new IllegalArgumentException("unknown material " + material + " for plane " + this);
        }
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

    TODO ab hier

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

    private void validateRectangleSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("invalid rectangle size: " + width + " x " + height);
        }
    }

    private void validateRectangle(int x, int y, int width, int height) {
        validateRectangleSize(width, height);
        validatePosition(x, y);
        validatePosition(x + width - 1, y + height - 1);
    }

    public void drawRectangle(int x, int y, int width, int height, int localMaterialIndex) {
        validateRectangle(x, y, width, height);
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
        validateRectangle(x, y, width, height);
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

    public byte[] copyToArray(int x, int y, int width, int height) {
        return copytoArrayInternal(x, y, width, height, null);
    }

    public void copyToArray(int x, int y, int width, int height, byte[] destination) {
        copytoArrayInternal(x, y, width, height, destination);
    }

    private byte[] copytoArrayInternal(int x, int y, int width, int height, byte[] destination) {
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
        if (destination == null) {
            destination = new byte[width * height];
        } else if (destination.length < width * height) {
            throw new IllegalArgumentException("array of size " + destination.length + " is too small, expected " + (width * height));
        }
        for (int dy = 0; dy < height; dy++) {
            System.arraycopy(pixels, (y + dy) * this.width + x, destination, dy * width, width);
        }
        return destination;
    }

    public void copyFormArray(int x, int y, int width, int height, byte[] source) {
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
        if (source.length < width * height) {
            throw new IllegalArgumentException("array of size " + source.length + " is too small, expected " + (width * height));
        }
        for (int dy = 0; dy < height; dy++) {
            System.arraycopy(source, dy * width, pixels, (y + dy) * this.width + x, width);
        }
    }

}
