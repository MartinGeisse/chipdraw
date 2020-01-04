package name.martingeisse.chipdraw.pixel.operation.scmos;

import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.operation.SnapshottingDesignOperation;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

public final class ScmosContactOperation extends SnapshottingDesignOperation {

    private final ScmosContactType contactType;
    private final int x, y;

    public ScmosContactOperation(ScmosContactType contactType, int x, int y) {
        this.contactType = contactType;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void doPerform(Design design) throws UserVisibleMessageException {
        ConceptSchemas.validateConformsUserVisible(design.getTechnology());
        design.getPlane(ConceptSchemas.PLANE_METAL1).drawRectangleAutoclip(x + 1, y + 1, 4, 4, ConceptSchemas.MATERIAL_METAL1);
        design.getPlane(ConceptSchemas.PLANE_METAL1).drawRectangleAutoclip(x + 2, y + 2, 2, 2, ConceptSchemas.MATERIAL_CONTACT);
        design.getPlane(contactType.getLowerMaterial().getPlaneSchema()).drawRectangleAutoclip(x, y, 6, 6, contactType.getLowerMaterial());
    }

}
