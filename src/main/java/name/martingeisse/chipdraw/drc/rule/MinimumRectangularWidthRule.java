package name.martingeisse.chipdraw.drc.rule;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.drc.DrcContext;

/**
 * Ensures that each pixel of the specified plane is part of an NxN-sized square, to ensure a minimum feature width.
 *
 * If separateMaterials is true, then that square must be filled with pixels of the same material. If separateMaterials
 * is false, then it is sufficient for the square to be filled with any nonempty pixels in the same plane.
 */
public class MinimumRectangularWidthRule implements Rule {

	private final PlaneSchema planeSchema;
	private final int size;
	private final boolean separateMaterials;
	private Plane plane;

	public MinimumRectangularWidthRule(PlaneSchema planeSchema, int size, boolean separateMaterials) {
		this.planeSchema = planeSchema;
		this.size = size;
		this.separateMaterials = separateMaterials;
	}

	@Override
	public void check(DrcContext context) {
		plane = context.getDesign().getPlane(planeSchema);
		for (int x = 0; x < plane.getWidth(); x++) {
			for (int y = 0; y < plane.getHeight(); y++) {
				if (checkPixel(x, y)) {
					context.report(x, y, "minimum width " + size + " in plane " + plane + " violated");
				}
			}
		}
	}

	private boolean checkPixel(int x, int y) {
		Material currentCenterMaterial = plane.getPixel(x, y);
		if (currentCenterMaterial == Material.NONE) {
			return true;
		}
		for (int dx = -size + 1; dx <= 0; dx++) {
			for (int dy = -size + 1; dy <= 0; dy++) {
				if (separateMaterials) {
					if (plane.isRectangleUniformAutoclip(x + dx, y + dy, size, size, currentCenterMaterial)) {
						return true;
					}
				} else {
					if (!plane.isRectangleContainsMaterialAutoclip(x + dx, y + dy, size, size, Material.NONE)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
