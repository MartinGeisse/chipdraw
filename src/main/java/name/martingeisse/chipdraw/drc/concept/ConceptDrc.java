package name.martingeisse.chipdraw.drc.concept;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.DrcContext;
import name.martingeisse.chipdraw.drc.rule.MinimumOverlapRule;
import name.martingeisse.chipdraw.drc.rule.MinimumRectangularWidthRule;
import name.martingeisse.chipdraw.drc.rule.Rule;

/**
 *
 */
public class ConceptDrc {

    private final ImmutableList<Rule> rules = ImmutableList.of(

            //
            // 1.x -- well rules
            //

			// 1.1 (Minimum width: 10)
			new MinimumRectangularWidthRule(Technologies.Concept.PLANE_WELL, 10, true),

            // 1.2 (Minimum spacing between wells at different potential) -- N/A because we don't have any
			// equal-implant wells at different potentials

			// 1.3 (Minimum spacing between wells at same potential: 6)
			// (incorporates 1.4 -- different-implant wells can be adjacent but not overlapping. We do not allow
			// overlap anyway since they are represented as different materials on the same plane).
			// TODO


			//
			// 2.x -- active
			//

			// 2.1 (Minimum width: 3)
			new MinimumRectangularWidthRule(Technologies.Concept.PLANE_DIFF, 3, true),

            // 2.2 (Minimum spacing, same implant: 3)
			// TODO







            new MinimumOverlapRule(
                    ImmutableSet.of(Technologies.Concept.MATERIAL_CONTACT),
                    ImmutableSet.of(Technologies.Concept.MATERIAL_CONTACT, Technologies.Concept.MATERIAL_METAL1),
                    1),
            new MinimumRectangularWidthRule(Technologies.Concept.PLANE_METAL1, 3, false),
            new MinimumOverlapRule(
                    ImmutableSet.of(Technologies.Concept.MATERIAL_VIA12),
                    ImmutableSet.of(Technologies.Concept.MATERIAL_CONTACT, Technologies.Concept.MATERIAL_METAL1),
                    1),
            new MinimumOverlapRule(
                    ImmutableSet.of(Technologies.Concept.MATERIAL_VIA12),
                    ImmutableSet.of(Technologies.Concept.MATERIAL_VIA12, Technologies.Concept.MATERIAL_METAL2),
                    1),
            new MinimumRectangularWidthRule(Technologies.Concept.PLANE_METAL2, 3, false)
    );

    public void perform(DrcContext context) {
        for (Rule rule : rules) {
            rule.check(context);
        }
    }

}
