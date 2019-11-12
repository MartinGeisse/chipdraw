package name.martingeisse.chipdraw.drc;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;

public final class Drc {

    public void perform(DrcContext context) {
        // sample DRC: ensures that plane 0 does not touch the design boundaries
        Plane plane = context.getDesign().getPlanes().get(0);
        for (int x = 0; x < plane.getWidth(); x++) {
            check(context, plane, x, 0);
            check(context, plane, x, plane.getHeight() - 1);
        }
        for (int y = 1; y < plane.getHeight() - 1; y++) {
            check(context, plane, 0, y);
            check(context, plane, plane.getWidth() - 1, y);
        }
    }

    private static void check(DrcContext context, Plane plane, int x, int y) {
        if (plane.getPixel(x, y) != Material.NONE) {
            context.report(x, y, "plane 0 must keep a padding of at least 1 pixel from the design boundary");
        }
    }

}
