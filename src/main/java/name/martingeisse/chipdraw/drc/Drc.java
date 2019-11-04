package name.martingeisse.chipdraw.drc;

import name.martingeisse.chipdraw.Plane;

public final class Drc {

    public void perform(DrcContext context) {
        // sample DRC: ensures that layer 0 does not touch the design boundaries
        Plane layer = context.getDesign().getLayers().get(0);
        for (int x = 0; x < layer.getWidth(); x++) {
            check(context, layer, x, 0);
            check(context, layer, x, layer.getHeight() - 1);
        }
        for (int y = 1; y < layer.getHeight() - 1; y++) {
            check(context, layer, 0, y);
            check(context, layer, layer.getWidth() - 1, y);
        }
    }

    private static void check(DrcContext context, Plane layer, int x, int y) {
        if (layer.getCell(x, y)) {
            context.report(x, y, "layer 0 must keep a padding of at least 1 cell from the design boundary");
        }
    }

}
