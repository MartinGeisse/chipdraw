package name.martingeisse.chipdraw.operation.library;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.operation.InPlaceDesignOperation;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

public abstract class AbstractDrawPointsOperation extends InPlaceDesignOperation {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private byte[] backup;

    public AbstractDrawPointsOperation(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    @Override
    protected void doPerform(Design design) throws UserVisibleMessageException {
        Plane plane = design.getPlane(getPlaneSchema());
        backup = plane.copyToArray(x, y, width, height);
        plane.drawRectangleAutoclip(x, y, width, height, getMaterial());
    }

    @Override
    protected void doUndo(Design design) throws UserVisibleMessageException {
        Plane plane = design.getPlane(getPlaneSchema());
        plane.copyFormArray(x, y, width, height, backup);
    }

    protected abstract PlaneSchema getPlaneSchema();
    protected abstract Material getMaterial();

}
