package name.martingeisse.chipdraw.drc.rule.experiment;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.drc.DrcContext;

public abstract class AbstractPerPixelRule extends AbstractRule {

    private final PlaneSchema pivotPlaneSchema;
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
        pivotPlane = context.getDesign().getPlane(pivotPlaneSchema);
        for (int pivotX = 0; pivotX < pivotPlane.getWidth(); pivotX++) {
            for (int pivotY = 0; pivotY < pivotPlane.getHeight(); pivotY++) {
                pivotMaterial = pivotPlane.getPixel(pivotX, pivotY);
                if (pivotMaterial != Material.NONE && !checkPixel()) {
                    context.report(pivotX, pivotY, getEffectiveErrorMessage());
                }
            }
        }
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
