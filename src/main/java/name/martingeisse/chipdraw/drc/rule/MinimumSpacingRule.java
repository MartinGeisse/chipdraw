package name.martingeisse.chipdraw.drc.rule;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.PlaneSchema;

/**
 * Splits the target plane into connected "patches" and ensures that a minimum spacing between patches is obeyed.
 *
 * This class uses a fixed value for the minimum spacing. Extend {@link AbstractMinimumSpacingRule} to determine
 * the spacing dynamically.
 */
public final class MinimumSpacingRule extends AbstractMinimumSpacingRule {

	private final int spacing;

	public MinimumSpacingRule(PlaneSchema planeSchema, MaterialMode materialMode, int spacing) {
		super(planeSchema, materialMode);
		this.spacing = spacing;
	}

	@Override
	protected int determineSpacing(int x, int y, Material material) {
		return spacing;
	}

	@Override
	protected String buildErrorMessage(int x, int y, Material material) {
		return "plane " + getPlaneSchema() + " violates minimum spacing of " + spacing;
	}

}
