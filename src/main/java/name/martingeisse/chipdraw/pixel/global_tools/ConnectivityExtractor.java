package name.martingeisse.chipdraw.pixel.global_tools;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;

/**
 *
 */
public abstract class ConnectivityExtractor extends AbstractPerPlaneExtractor {

    private final boolean mergeMaterials;
    private Material material = null;

    public ConnectivityExtractor(boolean mergeMaterials) {
        this.mergeMaterials = mergeMaterials;
    }

    @Override
    protected void handlePlane(Plane plane) {
        Plane copy = new Plane(plane);
        for (int y = 0; y < copy.getHeight(); y++) {
            for (int x = 0; x < copy.getWidth(); x++) {
                material = copy.getPixel(x, y);
                if (material != Material.NONE) {
                    beginPatch(mergeMaterials ? null : material);
                    clear(copy, x, y);
                    finishPatch();
                }
            }
        }
    }

    private void clear(Plane copy, int x, int y) {
        Material material = copy.getPixelAutoclip(x, y);
        if (mergeMaterials ? (material != Material.NONE) : (material == this.material)) {
            handlePixel(x, y);
            copy.setPixel(x, y, Material.NONE);
            clear(copy, x - 1, y);
            clear(copy, x + 1, y);
            clear(copy, x, y - 1);
            clear(copy, x, y + 1);
        }
    }

    protected void beginPatch(Material material) {
    }

    protected void handlePixel(int x, int y) {
    }

    protected void finishPatch() {
    }

    public static class Test extends ConnectivityExtractor {

        public Test() {
            super(false);
        }

        @Override
        protected void beginDesign(Design design) {
            System.out.println();
            System.out.println("*");
            System.out.println("* Connectivity extraction");
            System.out.println("*");
            System.out.println();
        }

        @Override
        protected boolean beginPlane(Plane plane) {
            System.out.println("* Plane");
            return true;
        }

        @Override
        protected void beginPatch(Material material) {
            System.out.println("found patch with local material index: " + material);
        }

    }

}
