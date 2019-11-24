package name.martingeisse.chipdraw.pixel.drc.rule;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.design.PlaneSchema;
import name.martingeisse.chipdraw.pixel.drc.DrcContext;

public abstract class AbstractPerPixelRule extends AbstractRule {

    private final PlaneSchema pivotPlaneSchema;
    private DrcContext context;
    private Plane pivotPlane;
    private int pivotX;
    private int pivotY;
    private Material pivotMaterial;

    public AbstractPerPixelRule(PlaneSchema pivotPlaneSchema) {
        this.pivotPlaneSchema = pivotPlaneSchema;
    }

    public final PlaneSchema getPivotPlaneSchema() {
        return pivotPlaneSchema;
    }

    @Override
    public void check(DrcContext context) {
        this.context = context;
        pivotPlane = context.getDesign().getPlane(pivotPlaneSchema);
        for (pivotX = 0; pivotX < pivotPlane.getWidth(); pivotX++) {
            for (pivotY = 0; pivotY < pivotPlane.getHeight(); pivotY++) {
                pivotMaterial = pivotPlane.getPixelAutoclip(pivotX, pivotY);
                if (pivotMaterial != Material.NONE && !checkPixel()) {
                    context.report(pivotX, pivotY, getEffectiveErrorMessage());
                }
            }
        }
    }

    public DrcContext getContext() {
        return context;
    }

    protected final Plane getPivotPlane() {
        return pivotPlane;
    }

    protected final int getPivotX() {
        return pivotX;
    }

    protected final int getPivotY() {
        return pivotY;
    }

    protected final Material getPivotMaterial() {
        return pivotMaterial;
    }

    protected abstract boolean checkPixel();

}
