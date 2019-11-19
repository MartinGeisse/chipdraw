package name.martingeisse.chipdraw.drc.rule;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.drc.DrcContext;

/**
 * Ensures that each pixel of the specified plane is part of an NxN-sized square, to ensure a minimum feature width.
 *
 * The materials of the target plane can be assigned to groups that treat all those materials the same. That is, each
 * pixel must be part of an NxN square of pixels which have any material from the same group. An example of such groups
 * would be (contact, metal1) for the metal1 plane, or (via12, metal2) for the metal2 plane, since a contact or via
 * is filled with metal in the metal plane during manufacturing -- the contact or via actually affects a different
 * process step. A counterexample would be (nwell, pwell) in the well plane -- these are actually different materials
 * in the well plane and so a narrow set of nwell pixels does not become sufficiently wide by placing pwell pixels
 * next to them.
 *
 * Any materials not associated with any group are treated as a one-element group, i.e. only that exact material can be
 * part of the NxN square.
 */
public class MinimumRectangularWidthRule implements Rule {

	private final PlaneSchema planeSchema;
	private final int size;
	private final boolean separateMaterials;
	private Plane plane;
	private Material currentCenterMaterial;

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
		currentCenterMaterial = plane.getPixel(x, y);
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
