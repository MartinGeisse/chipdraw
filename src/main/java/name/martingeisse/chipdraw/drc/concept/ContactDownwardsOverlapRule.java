package name.martingeisse.chipdraw.drc.concept;

import com.google.common.collect.ImmutableSet;
import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.DrcContext;
import name.martingeisse.chipdraw.drc.rule.MinimumOverlapRule;
import name.martingeisse.chipdraw.drc.rule.Rule;

/**
 * This rule is similar to a {@link MinimumOverlapRule}, but the overlap can occur in a specific way in two different
 * planes (diffusion and poly).
 */
public class ContactDownwardsOverlapRule implements Rule {

	// Actual minimum overlap is 1.5, but we cannot handle that yet, so we just use 2 to err on the safe side.
	// This applies to contacts both to diffusion and poly.
	private static final int OVERLAP = 2;
	private static final int SQUARE_SIZE = 2 * OVERLAP + 1;

	private Plane diffPlane;
	private Plane polyPlane;

	@Override
	public void check(DrcContext context) {
		Plane innerPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_METAL1);
		diffPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_DIFF);
		polyPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_POLY);
		for (int x = 0; x < innerPlane.getWidth(); x++) {
			for (int y = 0; y < innerPlane.getHeight(); y++) {
				if (innerPlane.getPixel(x, y) == Technologies.Concept.MATERIAL_CONTACT) {
					if (!checkPixel(x, y)) {
						context.report(x, y, "minimum overlap of contact with diffusion or poly violated");
					}
				}
			}
		}
	}

	private boolean checkPixel(int x, int y) {
		Plane outerPlane;
		Material outerMaterial = polyPlane.getPixel(x, y);
		if (outerMaterial != Material.NONE) {
			outerPlane = polyPlane;
		} else {
			outerMaterial = diffPlane.getPixel(x, y);
			if (outerMaterial != Material.NONE) {
				outerPlane = diffPlane;
			} else {
				return false;
			}
		}
		return outerPlane.isRectangleUniformAutoclip(x - OVERLAP, y - OVERLAP, SQUARE_SIZE, SQUARE_SIZE, outerMaterial);
	}

}
