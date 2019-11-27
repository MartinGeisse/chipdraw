package name.martingeisse.chipdraw.pnr.design;

import java.util.Arrays;

public final class RoutingPlane {

    private final int width, height;
    private final byte[] tileCodes;

    private RoutingPlane(int width, int height, byte[] dataSource) {
        this.width = width;
        this.height = height;
        this.tileCodes = new byte[width * height];
        if (dataSource == null) {
            Arrays.fill(tileCodes, RoutingTile.NONE.getCode());
        } else {
            System.arraycopy(dataSource, 0, tileCodes, 0, tileCodes.length);
        }
    }

    public RoutingPlane(int width, int height) {
        this(width, height, null);
    }

    public RoutingPlane(RoutingPlane original) {
        this(original.getWidth(), original.getHeight(), original.tileCodes);
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

    private int getIndex(int x, int y) {
        return isValidPosition(x, y) ? y * width + x : -1;
    }

    public RoutingTile getTile(int x, int y) {
        int index = getIndex(x, y);
        return index < 0 ? RoutingTile.NONE : RoutingTile.getForCode(tileCodes[index]);
    }

    public void setTile(int x, int y, RoutingTile tile) {
        int index = getIndex(x, y);
        if (index >= 0) {
            tileCodes[index] = tile.getCode();
        }
    }

    public void setEast(int x, int y, boolean connected) {
        int index = getIndex(x, y);
        if (index >= 0) {
            RoutingTile oldTile = RoutingTile.getForCode(tileCodes[index]);
            tileCodes[index] = oldTile.getWithEast(connected).getCode();
        }
    }

    public void setSouth(int x, int y, boolean connected) {
        int index = getIndex(x, y);
        if (index >= 0) {
            RoutingTile oldTile = RoutingTile.getForCode(tileCodes[index]);
            tileCodes[index] = oldTile.getWithSouth(connected).getCode();
        }
    }

    public void setDown(int x, int y, boolean connected) {
        int index = getIndex(x, y);
        if (index >= 0) {
            RoutingTile oldTile = RoutingTile.getForCode(tileCodes[index]);
            tileCodes[index] = oldTile.getWithDown(connected).getCode();
        }
    }

    public boolean isEmpty() {
        for (byte tileCode : tileCodes) {
            if (tileCode != RoutingTile.NONE.getCode()) {
                return false;
            }
        }
        return true;
    }

    public void copyFrom(RoutingPlane source) {
        if (source.getWidth() != getWidth() || source.getHeight() != getHeight()) {
            throw new IllegalArgumentException("source plane has different size");
        }
        copyFrom(source, 0, 0, 0, 0, getWidth(), getHeight());
    }

    public void copyFrom(RoutingPlane source, int sourceX, int sourceY, int destinationX, int destinationY, int rectangleWidth, int rectangleHeight) {
        validateRectangleSize(rectangleWidth, rectangleHeight);
        for (int dx = 0; dx < rectangleWidth; dx++) {
            for (int dy = 0; dy < rectangleHeight; dy++) {
                setTile(destinationX + dx, destinationY + dy, source.getTile(sourceX + dx, sourceY + dy));
            }
        }
    }

    private static void validateRectangleSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("invalid rectangle size: " + width + " x " + height);
        }
    }

}
