package name.martingeisse.chipdraw.pixel.drc.rule;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.PlaneSchema;

/**
 * Splits the target plane into connected "patches" and ensures that a minimum spacing between patches is obeyed.
 *
 * This class uses a fixed value for the minimum spacing. Extend {@link AbstractMinimumSelfSpacingRule} to determine
 * the spacing dynamically.
 */
public class MinimumSelfSpacingRule extends AbstractMinimumSelfSpacingRule {

	private final int spacing;

	public MinimumSelfSpacingRule(PlaneSchema planeSchema, MaterialMode materialMode, int spacing) {
		super(planeSchema, materialMode);
		this.spacing = spacing;
	}

	@Override
	protected String getImplicitErrorMessage() {
		return "plane " + getPlaneSchema() + " violates minimum spacing of " + spacing;
	}

	@Override
	protected final int determineSpacing(int x, int y, Material material) {
		return affects(x, y, material) ? spacing : -1;
	}

	protected boolean affects(int x, int y, Material material) {
		return true;
	}

}
