package name.martingeisse.chipdraw.pixel.drc.concept;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.design.Technologies;
import name.martingeisse.chipdraw.pixel.drc.DrcContext;
import name.martingeisse.chipdraw.pixel.drc.rule.AbstractPerPixelRule;

/**
 * This rule checks overlap of diffusion areas by wells.
 */
public class ActiveByWellOverlapRule extends AbstractPerPixelRule {

    private Plane wellPlane;

    public ActiveByWellOverlapRule() {
        super(Technologies.Concept.PLANE_DIFF);
    }

    @Override
    public String getImplicitErrorMessage() {
        return "minimum overlap of diffusion by well: 3 for well taps, 5 for source/drain";
    }

    @Override
    public void check(DrcContext context) {
        wellPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_WELL);
        super.check(context);
    }

    @Override
    protected boolean checkPixel() {
        int x = getPivotX();
        int y = getPivotY();
        Material wellPixel = wellPlane.getPixelAutoclip(x, y);
        if (wellPixel == Material.NONE) {
            return false;
        }
        Material diffPixel = getPivotMaterial();
        boolean ndiff = (diffPixel == Technologies.Concept.MATERIAL_NDIFF);
        boolean nwell = (wellPixel == Technologies.Concept.MATERIAL_NWELL);
        boolean sameType = (ndiff == nwell);
        int overlap = (sameType ? 3 : 5);
        return isSurroundedByMaterial(wellPlane, x, y, overlap, wellPixel);
    }

}
