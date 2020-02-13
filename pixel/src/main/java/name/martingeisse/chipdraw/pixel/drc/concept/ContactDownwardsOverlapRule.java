package name.martingeisse.chipdraw.pixel.drc.concept;

import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.drc.DrcContext;
import name.martingeisse.chipdraw.pixel.drc.rule.AbstractPerPixelRule;

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
        super(ConceptSchemas.PLANE_METAL1);
    }

    @Override
    public String getImplicitErrorMessage() {
        return "minimum overlap of contact with diffusion or poly: 2";
    }

    @Override
    public void check(DrcContext context) {
        diffPlane = context.getDesign().getPlane(ConceptSchemas.PLANE_DIFF);
        polyPlane = context.getDesign().getPlane(ConceptSchemas.PLANE_POLY);
        super.check(context);
    }

    @Override
    protected boolean checkPixel() {
        if (getPivotMaterial() != ConceptSchemas.MATERIAL_CONTACT) {
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
    }

}
