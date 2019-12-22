package name.martingeisse.chipdraw.pixel.operation.scmos;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Technologies;
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
        if (design.getTechnology() != Technologies.Concept.TECHNOLOGY) {
            throw new UserVisibleMessageException("this operation can only be performed on a CONCEPT design");
        }
        design.getPlane(Technologies.Concept.PLANE_METAL1).drawRectangleAutoclip(x + 1, y + 1, 4, 4, Technologies.Concept.MATERIAL_METAL1);
        design.getPlane(Technologies.Concept.PLANE_METAL1).drawRectangleAutoclip(x + 2, y + 2, 2, 2, Technologies.Concept.MATERIAL_CONTACT);
        design.getPlane(contactType.getLowerMaterial().getPlaneSchema()).drawRectangleAutoclip(x, y, 6, 6, contactType.getLowerMaterial());
    }

}
