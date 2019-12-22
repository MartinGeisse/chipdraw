package name.martingeisse.chipdraw.pixel.operation.scmos;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Technologies;
import name.martingeisse.chipdraw.pixel.operation.SnapshottingDesignOperation;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

public final class ScmosTransistorOperation extends SnapshottingDesignOperation {

    private final Material sourceDrainMaterial;
    private final int x, y;
    private final boolean horizontal;

    public ScmosTransistorOperation(Material sourceDrainMaterial, int x, int y, boolean horizontal) {
        this.sourceDrainMaterial = sourceDrainMaterial;
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }

    @Override
    protected void doPerform(Design design) throws UserVisibleMessageException {
        if (design.getTechnology() != Technologies.Concept.TECHNOLOGY) {
            throw new UserVisibleMessageException("this operation can only be performed on a CONCEPT design");
        }
        if (horizontal) {
            design.getPlane(Technologies.Concept.PLANE_DIFF).drawRectangleAutoclip(x, y, 6, 6, sourceDrainMaterial);
            design.getPlane(Technologies.Concept.PLANE_POLY).drawRectangleAutoclip(x - 2, y + 2, 10, 2, Technologies.Concept.MATERIAL_POLY);
        } else {
            design.getPlane(Technologies.Concept.PLANE_DIFF).drawRectangleAutoclip(x, y, 6, 6, sourceDrainMaterial);
            design.getPlane(Technologies.Concept.PLANE_POLY).drawRectangleAutoclip(x + 2, y - 2, 2, 10, Technologies.Concept.MATERIAL_POLY);
        }
    }

}
