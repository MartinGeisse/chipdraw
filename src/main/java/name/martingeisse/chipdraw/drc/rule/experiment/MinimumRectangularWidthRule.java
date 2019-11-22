package name.martingeisse.chipdraw.drc.rule.experiment;

import name.martingeisse.chipdraw.design.PlaneSchema;

/**
 * Ensures that each pixel of the specified plane is part of an NxN-sized square, to ensure a minimum feature width.
 * <p>
 * If separateMaterials is true, then that square must be filled with pixels of the same material. If separateMaterials
 * is false, then it is sufficient for the square to be filled with any nonempty pixels in the same plane.
 */
public final class MinimumRectangularWidthRule extends AbstractPerPixelRule {

    private final int size;
    private final boolean separateMaterials;

    public MinimumRectangularWidthRule(PlaneSchema pivotPlaneSchema, int size, boolean separateMaterials) {
        super(pivotPlaneSchema);
        this.size = size;
        this.separateMaterials = separateMaterials;
        setErrorMessage("minimum width " + size + " in plane " + pivotPlaneSchema);
    }

    @Override
    protected boolean checkPixel() {
        if (separateMaterials) {
            return hasMinimumWidthWithMaterial(getPivotPlane(), getPivotX(), getPivotY(), size, getPivotMaterial());
        } else {
            return hasMinimumWidthWithAnyMaterial(getPivotPlane(), getPivotX(), getPivotY(), size);
        }
    }

}
