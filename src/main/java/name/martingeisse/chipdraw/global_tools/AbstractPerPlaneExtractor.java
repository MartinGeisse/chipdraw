package name.martingeisse.chipdraw.global_tools;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Plane;

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

    public final void extract(Plane plane) {
        if (beginPlane(plane)) {
            handlePlane(plane);
            finishPlane(plane);
        }
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
