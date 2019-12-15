package name.martingeisse.chipdraw.pixel.operation.library;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.design.PlaneSchema;
import name.martingeisse.chipdraw.pixel.operation.InPlaceDesignOperation;
import name.martingeisse.chipdraw.pixel.util.RectangularSize;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

public abstract class AbstractDrawPointsOperation extends InPlaceDesignOperation {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private byte[] backup;

    public AbstractDrawPointsOperation(int x, int y, int width, int height, RectangularSize clipRegion) {
        if (clipRegion != null) {
            if (x < 0) {
                width += x;
                x = 0;
            }
            if (x + width > clipRegion.getWidth()) {
                width = clipRegion.getWidth() - x;
            }
            if (y < 0) {
                height += y;
                y = 0;
            }
            if (y + height > clipRegion.getHeight()) {
                height = clipRegion.getHeight() - y;
            }
        }
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
        backup = plane.copyToArray(x, y, width, height); // TODO does not handle out-of-bounds
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
