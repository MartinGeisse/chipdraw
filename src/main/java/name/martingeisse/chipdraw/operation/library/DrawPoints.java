package name.martingeisse.chipdraw.operation.library;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.PlaneSchema;

public final class DrawPoints extends AbstractDrawPointsOperation {

    private final Material material;

    public DrawPoints(int x, int y, int width, int height, Material material) {
        super(x, y, width, height);
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
