package name.martingeisse.chipdraw.pixel.design;

import name.martingeisse.chipdraw.pixel.util.RectangularSize;

import java.io.Serializable;
import java.util.Arrays;

/**
 * TODO it is probably a good idea to make the ...Autoclip() methods the normal behavior and remove the others. They
 * do not add a lot of safety against bugs and are mostly unused anyway.
 */
public final class Plane implements Serializable, RectangularSize {

	private static final long serialVersionUID = 1;

	private transient PlaneSchema planeSchema;
	private final int width, height;
	private final byte[] pixels;

	private Plane(PlaneSchema planeSchema, int width, int height, byte[] dataSource) {
		if (planeSchema == null) {
			throw new IllegalArgumentException("planeSchema cannot be null");
		}
		if (planeSchema.getMaterials().size() > Material.MAX_MATERIALS) {
			// so we can use bytes to store local material indices and also reserve a special byte value for EMPTY_PIXEL
			throw new IllegalArgumentException("more than " + Material.MAX_MATERIALS + " materials in a single plane currently not supported");
		}
		this.planeSchema = planeSchema;
		this.width = width;
		this.height = height;
		this.pixels = new byte[width * height];
		if (dataSource == null) {
			Arrays.fill(pixels, Material.EMPTY_PIXEL_CODE);
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
		return planeSchema.isMaterialValid(material);
	}

	public void validateMaterial(Material material) {
		planeSchema.validateMaterial(material);
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

	public Material getPixel(int x, int y) {
	    if (!isValidPosition(x, y)) {
	        return Material.NONE;
        }
		byte code = pixels[getIndex(x, y)];
		if (code == Material.EMPTY_PIXEL_CODE) {
			return Material.NONE;
		} else {
			return planeSchema.getMaterials().get(code & 0xff);
		}
	}

	public void setPixel(int x, int y, Material material) {
		validateMaterial(material);
        if (isValidPosition(x, y)) {
            pixels[getIndex(x, y)] = material.code;
        }
	}

	private void validateRectangleSize(int width, int height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("invalid rectangle size: " + width + " x " + height);
		}
	}

	public void drawRectangle(int x, int y, int width, int height, Material material) {
		validateRectangleSize(width, height);
        validateMaterial(material);
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
			if (width < 0) {
				width = 0;
			}
		}
		if (height > this.height - y) {
			height = this.height - y;
			if (height < 0) {
				height = 0;
			}
		}
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                setPixel(x + i, y + j, material);
            }
        }
	}

	public boolean isRectangleUniform(int x, int y, int width, int height, Material material) {
		validateRectangleSize(width, height);
		if (width == 0 || height == 0) {
			return true;
		}

		// handle non-clip case
		if (isValidPosition(x, y) && isValidPosition(x + width - 1, y + height - 1)) {
			return isRectangleUniformInternal(x, y, width, height, material);
		}

		// clipped case: at least one pixel is implicitly empty, so if we are looking for nonempty pixels, it can't be uniform
		if (material != Material.NONE) {
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
			if (width < 0) {
				width = 0;
			}
		}
		if (height > this.height - y) {
			height = this.height - y;
			if (height < 0) {
				height = 0;
			}
		}
		return isRectangleUniformInternal(x, y, width, height, material);
	}

	private boolean isRectangleUniformInternal(int x, int y, int width, int height, Material material) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (getPixel(x + i, y + j) != material) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isRectangleContainsMaterial(int x, int y, int width, int height, Material material) {
		validateRectangleSize(width, height);
		if (width == 0 || height == 0) {
			return false;
		}

		// handle non-clip case
		if (isValidPosition(x, y) && isValidPosition(x + width - 1, y + height - 1)) {
			return isRectangleContainsMaterialInternal(x, y, width, height, material);
		}

		// clipped case: at least one pixel is implicitly empty, so if we are looking for empty pixels, we have found them
		if (material == Material.NONE) {
			return true;
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
			if (width < 0) {
				width = 0;
			}
		}
		if (height > this.height - y) {
			height = this.height - y;
			if (height < 0) {
				height = 0;
			}
		}
		return isRectangleContainsMaterialInternal(x, y, width, height, material);
	}

	private boolean isRectangleContainsMaterialInternal(int x, int y, int width, int height, Material material) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (getPixel(x + i, y + j) == material) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isEmpty() {
		for (byte pixelValue : pixels) {
			if (pixelValue != Material.EMPTY_PIXEL_CODE) {
				return false;
			}
		}
		return true;
	}

	public boolean hasMaterial(Material material) {
		for (byte pixelValue : pixels) {
			if (pixelValue == material.code) {
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
			if (width < 0) {
				return new byte[0];
			}
		}
		if (height > this.height - y) {
			height = this.height - y;
			if (height < 0) {
				return new byte[0];
			}
		}
		byte[] destination = new byte[width * height];
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
			if (width < 0) {
				width = 0;
			}
		}
		if (height > this.height - y) {
			height = this.height - y;
			if (height < 0) {
				height = 0;
			}
		}
		if (source.length < width * height) {
			throw new IllegalArgumentException("array of size " + source.length + " is too small, expected " + (width * height));
		}
		for (int dy = 0; dy < height; dy++) {
			System.arraycopy(source, dy * width, pixels, (y + dy) * this.width + x, width);
		}
	}

	public static boolean rectangleEquals(Plane plane1, Plane plane2, int x, int y, int width, int height) {
		if (plane1.getSchema() != plane2.getSchema()) {
			throw new IllegalArgumentException("planes use different schemas");
		}
		for (int dx = 0; dx < width; dx++) {
			for (int dy = 0; dy < height; dy++) {
				int x2 = x + dx, y2 = y + dy;
				if (plane1.getPixel(x2, y2) != plane2.getPixel(x2, y2)) {
					return false;
				}
			}
		}
		return true;
	}

}
