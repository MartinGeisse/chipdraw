package name.martingeisse.chipdraw.operation.library;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.operation.InPlaceDesignOperation;
import name.martingeisse.chipdraw.util.Point;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

import java.util.HashMap;
import java.util.Map;

public final class DrawPoints extends InPlaceDesignOperation {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int globalMaterialIndex;
    private final Map<Point, Integer> originalPixels;

    public DrawPoints(int x, int y, int width, int height, int globalMaterialIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.globalMaterialIndex = globalMaterialIndex;
        this.originalPixels = new HashMap<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGlobalMaterialIndex() {
        return globalMaterialIndex;
    }

    @Override
    protected void doPerform(Design design) throws UserVisibleMessageException {
        int planeIndex = design.getTechnology().getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex);
        int localMaterialIndex = design.getTechnology().getLocalMaterialIndexForGlobalMaterialIndex(globalMaterialIndex);
        Plane plane = design.getPlanes().get(planeIndex);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixelX = x + i;
                int pixelY = y + j;
                // putIfAbsent ensures that when we are drawing the same pixel again, we are keeping the original old pixel
                originalPixels.putIfAbsent(new Point(pixelX, pixelY), plane.getPixel(pixelX, pixelY));
                plane.setPixel(pixelX, pixelY, localMaterialIndex);
            }
        }
    }

    @Override
    protected void doUndo(Design design) throws UserVisibleMessageException {
        int planeIndex = design.getTechnology().getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex);
        Plane plane = design.getPlanes().get(planeIndex);
        for (Map.Entry<Point, Integer> entry : originalPixels.entrySet()) {
            Point point = entry.getKey();
            plane.setPixel(point.getX(), point.getY(), entry.getValue());
        }
    }

}
