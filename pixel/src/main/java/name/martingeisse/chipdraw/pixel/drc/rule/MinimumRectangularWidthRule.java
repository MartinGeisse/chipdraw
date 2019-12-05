package name.martingeisse.chipdraw.pixel.drc.rule;

import name.martingeisse.chipdraw.pixel.design.PlaneSchema;

/**
 * Ensures that each pixel of the specified plane is part of an NxN-sized square, to ensure a minimum feature width.
 * <p>
 * If separateMaterials is true, then that square must be filled with pixels of the same material. If separateMaterials
 * is false, then it is sufficient for the square to be filled with any nonempty pixels in the same plane.
 * <p>
 * TODO: This rule cannot properly detect when two sufficiently wide areas are connected through a small path that
 * is too narrow IF each pixel of that path is part of one of the large areas, like this:
 *
 * ...####
 * ...####
 * ...####
 * #######
 * ####...
 * ####...
 * ####...
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
