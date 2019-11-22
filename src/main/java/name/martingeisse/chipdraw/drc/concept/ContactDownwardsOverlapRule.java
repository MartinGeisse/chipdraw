package name.martingeisse.chipdraw.drc.concept;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.DrcContext;
import name.martingeisse.chipdraw.drc.rule.MinimumOverlapRule;
import name.martingeisse.chipdraw.drc.rule.experiment.AbstractPerPixelRule;

/**
 * This rule is similar to a {@link MinimumOverlapRule}, but the overlap can occur in a specific way in two different
 * planes (diffusion and poly).
 */
public class ContactDownwardsOverlapRule extends AbstractPerPixelRule {

	// Actual minimum overlap is 1.5, but we cannot handle that yet, so we just use 2 to err on the safe side.
	// This applies to contacts both to diffusion and poly.
	private static final int OVERLAP = 2;
	private static final int SQUARE_SIZE = 2 * OVERLAP + 1;

	private Plane diffPlane;
	private Plane polyPlane;

	public ContactDownwardsOverlapRule() {
		super(Technologies.Concept.PLANE_METAL1);
	}

	@Override
	public void check(DrcContext context) {
		diffPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_DIFF);
		polyPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_POLY);
		super.check(context);
	}

	@Override
	protected boolean checkPixel() {
		if (getPivotMaterial() != Technologies.Concept.MATERIAL_CONTACT) {
			return true;
		}
		Plane overlappingPlane;
		Material overlappingMaterial = polyPlane.getPixel(getPivotX(), getPivotY());
		if (overlappingMaterial != Material.NONE) {
			overlappingPlane = polyPlane;
		} else {
			overlappingMaterial = diffPlane.getPixel(getPivotX(), getPivotY());
			if (overlappingMaterial != Material.NONE) {
				overlappingPlane = diffPlane;
			} else {
				return false;
			}
		}
		return isSurroundedByMaterial(overlappingPlane, getPivotX(), getPivotY(), OVERLAP, overlappingMaterial);

		TODO
//		return overlappingPlane.isRectangleUniformAutoclip(getPivotX() - OVERLAP, getPivotY() - OVERLAP,
//				SQUARE_SIZE, SQUARE_SIZE, overlappingMaterial);
	}

}
