package name.martingeisse.chipdraw.pixel.scmos.drc;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.drc.Drc;
import name.martingeisse.chipdraw.pixel.drc.DrcContext;
import name.martingeisse.chipdraw.pixel.drc.rule.*;

/**
 * Note: Rules have been checked manually for correctness and any deviation has been marked with a TODO marker (TODO: via1, rules 8.4 and 8.5).
 * For metal and via, only metal1 and via12 have been checked; higher layers use the same logic but different numbers,
 * so they should be easy to add once the number of layers and exact pixel numbers are known.
 * <p>
 * Note that pad / overglass rules are not implemented here because they are expressed in terms of microns, not lambda,
 * and we only know lambdas (i.e. we don't know how many pixels a micron is, so we cannot check those rules). For a
 * specific technology node, pad rules should be added.
 */
public class ScmosConceptDrc implements Drc {

    private static boolean isTransistorPixel(Design design, int x, int y) {
        if (design.getPlane(ConceptSchemas.PLANE_POLY).getPixel(x, y) == Material.NONE) {
            return false;
        }
        Material well = design.getPlane(ConceptSchemas.PLANE_WELL).getPixel(x, y);
        Material diff = design.getPlane(ConceptSchemas.PLANE_DIFF).getPixel(x, y);
        if (well == ConceptSchemas.MATERIAL_NWELL && diff == ConceptSchemas.MATERIAL_PDIFF) {
            return true;
        }
        if (well == ConceptSchemas.MATERIAL_PWELL && diff == ConceptSchemas.MATERIAL_NDIFF) {
            return true;
        }
        return false;
    }

    private final ImmutableList<Rule> rules = ImmutableList.of(

            //
            // 1.x -- well rules
            //

            // 1.1 (Minimum width: 10)
            new MinimumRectangularWidthRule(ConceptSchemas.PLANE_WELL, 10, true),

            // 1.2 (Minimum spacing between wells at different potential) -- N/A because we don't have any
            // equal-implant wells at different potentials

            // 1.3 (Minimum spacing between equal-implant wells at same potential: 6)
            new MinimumSelfSpacingRule(ConceptSchemas.PLANE_WELL, MinimumSelfSpacingRule.MaterialMode.IGNORE_OTHER_MATERIALS, 6),

            // 1.4 (Minimum spacing between wells of different type) is implicitly true because they are represented as
            // different materials on the same plane -- so they cannot overlap -- and the minimum spacing is 0.


            //
            // 2.x -- active
            //

            // 2.1 (Minimum width: 3)
            new MinimumRectangularWidthRule(ConceptSchemas.PLANE_DIFF, 3, true),

            // 2.2 (Minimum spacing, same implant: 3)
            new MinimumSelfSpacingRule(ConceptSchemas.PLANE_DIFF, MinimumSelfSpacingRule.MaterialMode.CHECK_OTHER_MATERIAL_SPACING, 3),

            // 2.3 (Source/drain active to well edge: 5)
            // 2.4 (Substrate/well contact active to well edge: 3)
            new ActiveByWellOverlapRule(),

            // 2.5 (Minimum spacing between non-abutting active of different implant: 4)
            new AbstractPerPixelRule(ConceptSchemas.PLANE_DIFF) {
                @Override
                protected boolean checkPixel() {
                    return !isMaterialNearby(getPivotPlane(), getPivotX(), getPivotY(), 4, getPivotMaterial().getOther());
                }
            }.setErrorMessageOverride("Minimum spacing between non-abutting active of different implant: 4"),


            //
            // 3.x -- poly
            //

            // 3.1 (Minimum width: 2)
            new MinimumRectangularWidthRule(ConceptSchemas.PLANE_POLY, 2, true),

            // 3.2 / 3.2a (Minimum spacing over field / active: 2)
            // Since the minimum spacing is the same over field and active, we can use a simple spacing rule.
            new MinimumSelfSpacingRule(ConceptSchemas.PLANE_POLY, MinimumSelfSpacingRule.MaterialMode.CHECK_OTHER_MATERIAL_SPACING, 2),

            // 3.3 (Minimum gate extension of [over] active: 2)
            new AbstractPerPixelRule(ConceptSchemas.PLANE_POLY) {
                @Override
                protected boolean checkPixel() {
                    int x = getPivotX(), y = getPivotY();
                    if (isTransistorPixel(getContext().getDesign(), x, y)) {
                        return hasMinimumExtensionWithAnyMaterial(getPivotPlane(), x, y, 2);
                    }
                    return true;
                }
            }.setErrorMessageOverride("Minimum gate extension over active: 2"),

            // 3.4 (Minimum active extension of [over] poly: 3)
            new AbstractPerPixelRule(ConceptSchemas.PLANE_DIFF) {
                @Override
                protected boolean checkPixel() {
                    int x = getPivotX(), y = getPivotY();
                    if (isTransistorPixel(getContext().getDesign(), x, y)) {
                        return hasMinimumExtensionWithMaterial(getPivotPlane(), x, y, 3, getPivotMaterial());
                    }
                    return true;
                }
            }.setErrorMessageOverride("Minimum active extension over poly: 3"),

            // 3.5 (Minimum field poly [spacing] to active: 1)
            new MinimumFieldPolyOverActiveRule(),


            //
            // 4.x -- select
            //

            // 4.1 (Minimum select spacing to channel of transistor to ensure adequate source/drain width: 3)
            // Along Manhattan directions, this is already checked by 3.4 (minimum active extension of poly). I'm
            // not sure if the rule applies to diagonal directions since it says "to ensure adequate source/drain
            // width" -- seems to apply to abutting implant regions instead. A rule that also checks diagonally would
            // probably complain about nearby well taps unnecessarily.
            // TODO ask

            // 4.2 (Minimum select overlap of active)
            // is implicitly obeyed since we derive the select mask automatically

            // 4.3 (Minimum select overlap of contact: 1)
            // is implicitly obeyed since we derive the select mask automatically and because of minimum active overlap
            // of contact

            // 4.4 (Minimum select width and spacing (Note: P-select and N-select may be coincident, but must not overlap))
            // is implicitly obeyed since we derive the select mask automatically

            //
            // 5.x -- contact to poly
            // 6.x -- contact to active
            //

            // 5.1 and 6.1 (Exact contact size: 2x2)
            new ExactMaterialSizeRule(ConceptSchemas.MATERIAL_CONTACT, 2),

            // 5.2 (Minimum poly overlap: 1.5 -> 2) and 6.2 (Minimum active overlap: 1.5 -> 2)
            new ContactDownwardsOverlapRule(),

            // 5.3 and 6.3 (Minimum contact spacing: 2)
            new MinimumSelfSpacingRule(ConceptSchemas.PLANE_METAL1, AbstractMinimumSelfSpacingRule.MaterialMode.IGNORE_OTHER_MATERIALS, 2) {
                @Override
                protected boolean affects(int x, int y, Material material) {
                    return material == ConceptSchemas.MATERIAL_CONTACT;
                }
            }.setErrorMessageOverride("Minimum contact spacing: 2"),

            // 5.4 and 6.4 (Minimum spacing to gate of transistor: 2)
            new AbstractPerPixelRule(ConceptSchemas.PLANE_METAL1) {
                @Override
                protected boolean checkPixel() {
                    if (getPivotMaterial() != ConceptSchemas.MATERIAL_CONTACT) {
                        return true;
                    }
                    int x = getPivotX(), y = getPivotY();
                    int distance = 2;
                    for (int dx = -distance; dx <= distance; dx++) {
                        for (int dy = -distance; dy <= distance; dy++) {
                            if (isTransistorPixel(getContext().getDesign(), x + dx, y + dy)) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }.setErrorMessageOverride("Minimum spacing of contact to gate of transistor: 2"),


            //
            // 7.x -- metal1
            //

            // 7.1 (Minimum width: 3)
            new MinimumRectangularWidthRule(ConceptSchemas.PLANE_METAL1, 3, false),

            // 7.2 (Minimum spacing: 2)
            new MinimumSelfSpacingRule(ConceptSchemas.PLANE_METAL1, MinimumSelfSpacingRule.MaterialMode.MERGE_MATERIALS, 2),

            // 7.3 (Minimum overlap of any contact: 1)
            new AbstractPerPixelRule(ConceptSchemas.PLANE_METAL1) {
                @Override
                protected boolean checkPixel() {
                    if (getPivotMaterial() != ConceptSchemas.MATERIAL_CONTACT) {
                        return true;
                    }
                    int x = getPivotX(), y = getPivotY();
                    return isSurroundedByAnyMaterial(getPivotPlane(), x, y, 1);
                }
            }.setErrorMessageOverride("Minimum overlap of contact with metal1: 1"),

            // 7.4 (Minimum spacing when either metal line is wider than 10 lambda: 4)
            // TODO


            //
            // 8.x -- via [via12]
            //

            // 8.1 (Exact size: 2x2)
            new ExactMaterialSizeRule(ConceptSchemas.MATERIAL_VIA12, 2),

            // 8.2 (Minimum via1 spacing: 3)
            new MinimumSelfSpacingRule(ConceptSchemas.PLANE_METAL2, AbstractMinimumSelfSpacingRule.MaterialMode.IGNORE_OTHER_MATERIALS, 3) {
                @Override
                protected boolean affects(int x, int y, Material material) {
                    return material == ConceptSchemas.MATERIAL_VIA12;
                }
            }.setErrorMessageOverride("Minimum via12 spacing: 3"),

            // 8.3 (Minimum overlap [of via12] by metal1: 1)
            // includes 9.3 (Minimum overlap of via12 [by metal2]: 1)
            new AbstractPerPixelRule(ConceptSchemas.PLANE_METAL2) {
                @Override
                protected boolean checkPixel() {
                    if (getPivotMaterial() != ConceptSchemas.MATERIAL_VIA12) {
                        return true;
                    }
                    Plane metal1 = getContext().getDesign().getPlane(ConceptSchemas.PLANE_METAL1);
                    Plane metal2 = getPivotPlane();
                    int x = getPivotX(), y = getPivotY();
                    return isSurroundedByAnyMaterial(metal1, x, y, 1) && isSurroundedByAnyMaterial(metal2, x, y, 1);
                }
            }.setErrorMessageOverride("Minimum overlap of via12 by metal1 and metal2: 1"),

            // 8.4 TODO -- are stacked vias allowed?

            // 8.5 TODO -- are stacked vias allowed?


            //
            // 9.x -- metal2
            //

            // 9.1 (Minimum width: 3)
            new MinimumRectangularWidthRule(ConceptSchemas.PLANE_METAL2, 3, false),

            // 9.2 (Minimum spacing: 3)
            new MinimumSelfSpacingRule(ConceptSchemas.PLANE_METAL2, MinimumSelfSpacingRule.MaterialMode.MERGE_MATERIALS, 3)

            // 9.3 (Minimum overlap of via1: 1): grouped with 8.3

            // 9.4 (Minimum spacing when either metal line is wider than 10 lambda: 6)
            // TODO

    );

    public void perform(DrcContext context) {
        for (Rule rule : rules) {
            rule.check(context);
        }
    }

}
