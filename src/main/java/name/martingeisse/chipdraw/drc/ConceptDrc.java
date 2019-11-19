package name.martingeisse.chipdraw.drc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.rule.MinimumOverlapRule;
import name.martingeisse.chipdraw.drc.rule.MinimumRectangularWidthRule;
import name.martingeisse.chipdraw.drc.rule.Rule;

/**
 *
 */
public class ConceptDrc {

	private final ImmutableList<Rule> rules = ImmutableList.of(
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
