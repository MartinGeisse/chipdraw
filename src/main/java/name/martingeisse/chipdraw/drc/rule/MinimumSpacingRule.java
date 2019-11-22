package name.martingeisse.chipdraw.drc.rule;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.PlaneSchema;

/**
 * Splits the target plane into connected "patches" and ensures that a minimum spacing between patches is obeyed.
 *
 * This class uses a fixed value for the minimum spacing. Extend {@link AbstractMinimumSpacingRule} to determine
 * the spacing dynamically.
 */
public class MinimumSpacingRule extends AbstractMinimumSpacingRule {

	private final int spacing;

	public MinimumSpacingRule(PlaneSchema planeSchema, MaterialMode materialMode, int spacing) {
		super(planeSchema, materialMode);
		this.spacing = spacing;
	}

	@Override
	protected final int determineSpacing(int x, int y, Material material) {
		return affects(x, y, material) ? spacing : -1;
	}

	protected boolean affects(int x, int y, Material material) {
		return true;
	}

	@Override
	protected final String buildErrorMessage(int x, int y, Material material) {
		return "plane " + getPlaneSchema() + " violates minimum spacing of " + spacing;
	}

}
