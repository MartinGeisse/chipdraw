package name.martingeisse.chipdraw.pnr.util;

public interface RectangularSize {

    int getWidth();
    int getHeight();

    default boolean isRectangleFullyInside(int x, int y, int w, int h) {
        if (w < 0 || h < 0) {
            throw new IllegalArgumentException("invalid rectangle size: " + w + " x " + y);
        }
        return (x >= 0 && y >= 0 && x + w <= getWidth() && y + h <= getHeight());
    }

    default void validateSubRectangle(int x, int y, int w, int h) {
        if (!isRectangleFullyInside(x, y, w, h)) {
            throw new IllegalArgumentException("rectangle out of bounds: (" + x + ", " + y + ") size (" + w + " x " + h + ")");
        }
    }

}
