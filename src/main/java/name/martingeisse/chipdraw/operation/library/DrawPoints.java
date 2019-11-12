package name.martingeisse.chipdraw.operation.library;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.operation.InPlaceDesignOperation;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

public final class DrawPoints extends InPlaceDesignOperation {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Material material;
    private byte[] backup;

    public DrawPoints(int x, int y, int width, int height, Material material) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.material = material;
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

    public Material getMaterial() {
        return material;
    }

    @Override
    protected void doPerform(Design design) throws UserVisibleMessageException {
        PlaneSchema planeSchema = material.getPlaneSchema();
        Plane plane = design.getPlane(planeSchema);
        backup = plane.copyToArray(x, y, width, height);
        plane.drawRectangleAutoclip(x, y, width, height, material);
    }

    @Override
    protected void doUndo(Design design) throws UserVisibleMessageException {
        PlaneSchema planeSchema = material.getPlaneSchema();
        Plane plane = design.getPlane(planeSchema);
        plane.copyFormArray(x, y, width, height, backup);
    }

}
