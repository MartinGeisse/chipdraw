package name.martingeisse.chipdraw.drc.concept;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.DrcContext;
import name.martingeisse.chipdraw.drc.rule.AbstractPerPixelRule;

/**
 * This rule checks overlap of contacts by poly or diffusion.
 */
public class ContactDownwardsOverlapRule extends AbstractPerPixelRule {

    // Actual minimum overlap is 1.5, but we cannot handle that yet, so we just use 2 to err on the safe side.
    // This applies to contacts both to diffusion and poly.
    private static final int OVERLAP = 2;

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
        Material overlappingMaterial = polyPlane.getPixelAutoclip(getPivotX(), getPivotY());
        if (overlappingMaterial != Material.NONE) {
            overlappingPlane = polyPlane;
        } else {
            overlappingMaterial = diffPlane.getPixelAutoclip(getPivotX(), getPivotY());
            if (overlappingMaterial != Material.NONE) {
                overlappingPlane = diffPlane;
            } else {
                return false;
            }
        }
        return isSurroundedByMaterial(overlappingPlane, getPivotX(), getPivotY(), OVERLAP, overlappingMaterial);
    }

}
