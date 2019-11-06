package name.martingeisse.chipdraw.global_tools;

import name.martingeisse.chipdraw.Design;

/**
 * Adds an N-pixel boundary on all sides of a design. The result is returned as a new design object.
 */
public final class Enlarger {

    private final Design design;
    private final int boundarySize;

    public Enlarger(Design design, int boundarySize) {
        if (boundarySize < 1) {
            throw new IllegalArgumentException("invalid enlarger boundary size: " + boundarySize);
        }
        this.design = design;
        this.boundarySize = boundarySize;
    }

    public Enlarger(Design design) {
        this(design, 10);
    }

    public Design enlarge() {
        Design out = new Design(design.getTechnology(), design.getWidth() + 2 * boundarySize, design.getHeight() + 2 * boundarySize);
        out.copyFrom(design, 0, 0, boundarySize, boundarySize, design.getWidth(), design.getHeight());
        return out;
    }

}
