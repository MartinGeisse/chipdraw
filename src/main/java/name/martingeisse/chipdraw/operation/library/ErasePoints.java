package name.martingeisse.chipdraw.operation.library;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.PlaneSchema;

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
