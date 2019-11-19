package name.martingeisse.chipdraw.drc.rule;

import com.google.common.collect.ImmutableSet;
import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.drc.DrcContext;

/**
 * Ensures that each pixel of the specified "inner" plane and material(s) is surrounded by at least N pixels of the
 * "outer" plane and material(s), that is, for each pixel in the "inner" plane which has an "inner" material,
 * the (2N+1)x(2N+1) square centered at that pixel is fully filled with acceptable "outer" material pixels in the
 * "outer" plane.
 */
public class MinimumOverlapRule implements Rule {

	private final PlaneSchema innerPlaneSchema;
	private final ImmutableSet<Material> innerMaterials;
	private final PlaneSchema outerPlaneSchema;
	private final ImmutableSet<Material> outerMaterials;
	private final int overlap;
	private Plane outerPlane;

	public MinimumOverlapRule(ImmutableSet<Material> innerMaterials, ImmutableSet<Material> outerMaterials, int overlap) {

		if (innerMaterials == null || innerMaterials.isEmpty()) {
			throw new IllegalArgumentException("innerMaterials cannot be null or empty");
		}
		this.innerPlaneSchema = innerMaterials.iterator().next().getPlaneSchema();
		for (Material material : innerMaterials) {
			if (material.getPlaneSchema() != innerPlaneSchema) {
				throw new IllegalArgumentException("inner materials are located in multiple planes");
			}
		}
		this.innerMaterials = innerMaterials;

		if (outerMaterials == null || outerMaterials.isEmpty()) {
			throw new IllegalArgumentException("outerMaterials cannot be null or empty");
		}
		this.outerPlaneSchema = outerMaterials.iterator().next().getPlaneSchema();
		for (Material material : outerMaterials) {
			if (material.getPlaneSchema() != outerPlaneSchema) {
				throw new IllegalArgumentException("outer materials are located in multiple planes");
			}
		}
		this.outerMaterials = outerMaterials;

		if (overlap < 0) {
			throw new IllegalArgumentException("overlap cannot be negative");
		}
		this.overlap = overlap;

	}

	@Override
	public void check(DrcContext context) {
		Plane innerPlane = context.getDesign().getPlane(innerPlaneSchema);
		outerPlane = context.getDesign().getPlane(outerPlaneSchema);
		for (int x = 0; x < innerPlane.getWidth(); x++) {
			for (int y = 0; y < innerPlane.getHeight(); y++) {
				if (innerMaterials.contains(innerPlane.getPixel(x, y))) {
					if (!checkPixel(x, y)) {
						context.report(x, y, "minimum overlap of " + innerMaterials + " with " + outerMaterials + " violated");
					}
				}
			}
		}
	}

	private boolean checkPixel(int x, int y) {
		for (int dx = -overlap; dx <= overlap; dx++) {
			for (int dy = -overlap; dy <= overlap; dy++) {
				if (!outerMaterials.contains(outerPlane.getPixel(x + dy, y + dy))) {
					return false;
				}
			}
		}
		return true;
	}

}
