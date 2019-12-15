package name.martingeisse.chipdraw.pixel.operation.library;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.PlaneSchema;
import name.martingeisse.chipdraw.pixel.util.RectangularSize;

public final class DrawPoints extends AbstractDrawPointsOperation {

    private final Material material;

    public DrawPoints(int x, int y, int width, int height, Material material, RectangularSize clipRegion) {
        super(x, y, width, height, clipRegion);
        this.material = material;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    protected PlaneSchema getPlaneSchema() {
        return material.getPlaneSchema();
    }

}
