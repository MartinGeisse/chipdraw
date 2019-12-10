package name.martingeisse.chipdraw.pixel.global_tools;

import name.martingeisse.chipdraw.pixel.design.Design;

/**
 * Adds an N-pixel boundary on all sides of a design. The result is returned as a new design object.
 */
public final class Enlarger {

	private final Design design;
	private final int boundarySize;
	private final boolean enlargeX, enlargeY;

	public Enlarger(Design design, int boundarySize) {
        this(design, boundarySize, true, true);
	}

	public Enlarger(Design design, int boundarySize, boolean enlargeX, boolean enlargeY) {
		if (boundarySize < 1) {
			throw new IllegalArgumentException("invalid enlarger boundary size: " + boundarySize);
		}
		this.design = design;
		this.boundarySize = boundarySize;
		this.enlargeX = enlargeX;
		this.enlargeY = enlargeY;
	}

	public Design enlarge() {
		int newWidth = design.getWidth() + (enlargeX ? 2 * boundarySize : 0);
		int newHeight = design.getHeight() + (enlargeY ? 2 * boundarySize : 0);
		Design out = new Design(design.getTechnology(), newWidth, newHeight);
		out.copyFrom(design, 0, 0, enlargeX ? boundarySize : 0, enlargeY ? boundarySize : 0, design.getWidth(), design.getHeight());
		return out;
	}

}
