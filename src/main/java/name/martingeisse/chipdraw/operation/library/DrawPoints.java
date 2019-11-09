package name.martingeisse.chipdraw.operation.library;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.operation.InPlaceDesignOperation;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

public final class DrawPoints extends InPlaceDesignOperation {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int globalMaterialIndex;
    private byte[] backup;

    public DrawPoints(int x, int y, int width, int height, int globalMaterialIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.globalMaterialIndex = globalMaterialIndex;
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
        backup = plane.copyToArray(x, y, width, height);
        plane.drawRectangleAutoclip(x, y, width, height, localMaterialIndex);
    }

    @Override
    protected void doUndo(Design design) throws UserVisibleMessageException {
        int planeIndex = design.getTechnology().getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex);
        Plane plane = design.getPlanes().get(planeIndex);
        plane.copyFormArray(x, y, width, height, backup);
    }

}
