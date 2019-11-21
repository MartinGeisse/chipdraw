package name.martingeisse.chipdraw.drc.concept;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.DrcContext;
import name.martingeisse.chipdraw.drc.rule.MinimumOverlapRule;
import name.martingeisse.chipdraw.drc.rule.Rule;

/**
 * This rule is similar to a {@link MinimumOverlapRule}, checking overlap of diffusion areas by wells, but the
 * minimum overlap depends on whether the diffusion area and the well use the same or opposite implant type.
 */
public class ActiveByWellOverlapRule implements Rule {

    private Plane wellPlane;
    private Plane diffPlane;
    private Material diffPixel;

    @Override
    public void check(DrcContext context) {
        wellPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_WELL);
        diffPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_DIFF);
        for (int x = 0; x < diffPlane.getWidth(); x++) {
            for (int y = 0; y < diffPlane.getHeight(); y++) {
                diffPixel = diffPlane.getPixel(x, y);
                if (diffPixel != Material.NONE) {
                    if (!checkPixel(x, y)) {
                        context.report(x, y, "minimum overlap of contact with diffusion or poly violated");
                    }
                }
            }
        }
    }

    private boolean checkPixel(int x, int y) {
        Material wellPixel = wellPlane.getPixel(x, y);
        if (wellPixel == Material.NONE) {
			return false;
		}
        boolean ndiff = (diffPixel == Technologies.Concept.MATERIAL_NDIFF);
        boolean nwell = (wellPixel == Technologies.Concept.MATERIAL_NWELL);
        boolean sameType = (ndiff == nwell);
        int overlap = (sameType ? 3 : 5);


        TODO

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
