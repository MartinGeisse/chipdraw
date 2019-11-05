package name.martingeisse.chipdraw.extractor;

import name.martingeisse.chipdraw.Design;
import name.martingeisse.chipdraw.Plane;

public abstract class AbstractPerPlaneExtractor {

    public final void extract(Design design) {
        beginDesign(design);
        for (Plane plane : design.getPlanes()) {
            if (beginPlane(plane)) {
                handlePlane(plane);
                finishPlane(plane);
            }
        }
        finishDesign(design);
    }

    protected void beginDesign(Design design) {
    }

    protected void finishDesign(Design design) {
    }

    protected boolean beginPlane(Plane plane) {
        return true;
    }

    protected abstract void handlePlane(Plane plane);

    protected void finishPlane(Plane plane) {
    }

}
