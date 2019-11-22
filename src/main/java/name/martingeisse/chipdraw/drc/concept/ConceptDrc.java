package name.martingeisse.chipdraw.drc.concept;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.DrcContext;
import name.martingeisse.chipdraw.drc.rule.MinimumOverlapRule;
import name.martingeisse.chipdraw.drc.rule.experiment.MinimumRectangularWidthRule;
import name.martingeisse.chipdraw.drc.rule.MinimumSpacingRule;
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

			// 1.3 (Minimum spacing between equal-implant wells at same potential: 6)
			new MinimumSpacingRule(Technologies.Concept.PLANE_WELL, MinimumSpacingRule.MaterialMode.IGNORE_OTHER_MATERIALS, 6),

			// 1.4 (Minimum spacing between wells of different type) is implicitly true because they are represented as
			// different materials on the same plane.


			//
			// 2.x -- active
			//

			// 2.1 (Minimum width: 3)
			new MinimumRectangularWidthRule(Technologies.Concept.PLANE_DIFF, 3, true),

            // 2.2 (Minimum spacing, same implant: 3)
			new MinimumSpacingRule(Technologies.Concept.PLANE_DIFF, MinimumSpacingRule.MaterialMode.CHECK_OTHER_MATERIAL_SPACING, 3),

            // 2.3 (Source/drain active to well edge: 5)
			// TODO overlap rule, but we need to dynamically determine the size from the center pixel and materials in multiple planes

			// 2.4 (Substrate/well contact active to well edge: 3)
			// TODO overlap rule, but we need to dynamically determine the size from the center pixel and materials in multiple planes

			// 2.5 (Minimum spacing between non-abutting active of different implant: 4)
			// TODO spacing rule but we need to dynamically determine the width


			//
			// 3.x -- poly
			//

			// 3.1 (Minimum width: 2)
			new MinimumRectangularWidthRule(Technologies.Concept.PLANE_POLY, 2, true),

            // 3.2 / 3.2a (Minimum spacing over field / active: 2)
			// Since the minimum spacing is the same over field and active, we can use a simple spacing rule.
			new MinimumSpacingRule(Technologies.Concept.PLANE_POLY, MinimumSpacingRule.MaterialMode.CHECK_OTHER_MATERIAL_SPACING, 2),

			// 3.3 (Minimum gate extension of active: 2)
			// TODO

			// 3.4 (Minimum active extension of poly: 3)
			// TODO

			// 3.5 (Minimum field poly [spacing] to active: 1)
			// TODO


			//
			// 4.x -- select
			//

			// 4.1 (Minimum select spacing to channel of transistor to ensure adequate source/drain width: 3)
			// TODO

			// 4.2 (Minimum select overlap of active) is implicitly obeyed since we derive the select mask automatically

			// 4.3 (Minimum select overlap of contact: 1)
			// TODO

			// 4.4 (Minimum select width and spacing (Note: P-select and N-select may be coincident, but must not overlap))
			// TODO

			//
			// 5.x -- contact to poly
			// 6.x -- contact to active
			//

			// 5.1 and 6.1 (Exact contact size: 2x2)
			// TODO

			// 5.2 (Minimum poly overlap: 1.5 -> 2) and 6.2 (Minimum active overlap: 1.5 -> 2)
			new ContactDownwardsOverlapRule(),

			// 5.3 and 6.3 (Minimum contact spacing: 2)
			// TODO

			// 5.4 and 6.4 (Minimum spacing to gate of transistor: 2)
			// TODO


			//
			// 7.x -- metal1
			//

			// 7.1 (Minimum width: 3)
			new MinimumRectangularWidthRule(Technologies.Concept.PLANE_METAL1, 3, false),

			// 7.2 (Minimum spacing: 2)
			new MinimumSpacingRule(Technologies.Concept.PLANE_METAL1, MinimumSpacingRule.MaterialMode.MERGE_MATERIALS, 2),

			// 7.3 (Minimum overlap of any contact: 1)
			new MinimumOverlapRule(
						ImmutableSet.of(Technologies.Concept.MATERIAL_CONTACT),
						ImmutableSet.of(Technologies.Concept.MATERIAL_CONTACT, Technologies.Concept.MATERIAL_METAL1),
						1),

			// 7.4 (Minimum spacing when either metal line is wider than 10 lambda: 4)
			// TODO


			//
			// 8.x -- via [via12]
			//

			// 8.1 (Exact size: 2x2)
			// TODO

			// 8.2 (Minimum via1 spacing)
			// TODO –- note that 9.2 ensures spacing only against unconnected vias. We need a spacing rule here that
			// checks only for plane_metal2 == via12, not for plane_metal2 == metal2

			// 8.3 (Minimum overlap by metal1: 1)
            new MinimumOverlapRule(
                    ImmutableSet.of(Technologies.Concept.MATERIAL_VIA12),
                    ImmutableSet.of(Technologies.Concept.MATERIAL_CONTACT, Technologies.Concept.MATERIAL_METAL1),
                    1),

			// 8.4 TODO -- are stacked vias allowed?

			// 8.5 TODO -- are stacked vias allowed?


			//
			// 9.x -- metal2
			//

			// 9.1 (Minimum width: 3)
            new MinimumRectangularWidthRule(Technologies.Concept.PLANE_METAL2, 3, false),

			// 9.2 (Minimum spacing: 3)
			new MinimumSpacingRule(Technologies.Concept.PLANE_METAL2, MinimumSpacingRule.MaterialMode.MERGE_MATERIALS, 3),

			// 9.3 (Minimum overlap of via1: 1)
            new MinimumOverlapRule(
                    ImmutableSet.of(Technologies.Concept.MATERIAL_VIA12),
                    ImmutableSet.of(Technologies.Concept.MATERIAL_VIA12, Technologies.Concept.MATERIAL_METAL2),
                    1)

			// 9.4 (Minimum spacing when either metal line is wider than 10 lambda: 6)
			// TODO

			// TODO pad rules, especially "Minimum pad spacing to active, poly or poly2: 15µ"

    );

    public void perform(DrcContext context) {
        for (Rule rule : rules) {
            rule.check(context);
        }
    }

}
