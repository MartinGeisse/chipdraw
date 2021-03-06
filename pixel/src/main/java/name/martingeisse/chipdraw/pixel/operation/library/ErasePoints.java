package name.martingeisse.chipdraw.pixel.operation.library;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.PlaneSchema;
import name.martingeisse.chipdraw.pixel.util.RectangularSize;

public final class ErasePoints extends AbstractDrawPointsOperation {

    private final PlaneSchema planeSchema;

    public ErasePoints(int x, int y, int width, int height, PlaneSchema planeSchema) {
        super(x, y, width, height);
        this.planeSchema = planeSchema;
    }

    @Override
    public Material getMaterial() {
        return Material.NONE;
    }

    @Override
    protected PlaneSchema getPlaneSchema() {
        return planeSchema;
    }

}
