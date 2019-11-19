package name.martingeisse.chipdraw.drc;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.rule.MinimumRectangularWidthRule;
import name.martingeisse.chipdraw.drc.rule.Rule;

/**
 *
 */
public class ConceptDrc {

	private final ImmutableList<Rule> rules = ImmutableList.of(
		new MinimumRectangularWidthRule(Technologies.Concept.PLANE_METAL1, 3, false),
		new MinimumRectangularWidthRule(Technologies.Concept.PLANE_METAL2, 3, false)
	);

	public void perform(DrcContext context) {
		for (Rule rule : rules) {
			rule.check(context);
		}
	}

}
