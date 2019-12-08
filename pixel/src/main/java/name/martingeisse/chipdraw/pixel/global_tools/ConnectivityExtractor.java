package name.martingeisse.chipdraw.pixel.global_tools;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;

/**
 *
 */
public abstract class ConnectivityExtractor extends AbstractPerPlaneExtractor {

    private final boolean mergeMaterials;
    private Plane planeCopy;
    private Material patchMaterial;
    private int[] todoX = new int[100], todoY = new int[100];
    private int todoCount;

    public ConnectivityExtractor(boolean mergeMaterials) {
        this.mergeMaterials = mergeMaterials;
    }

    @Override
    protected void handlePlane(Plane plane) {
        planeCopy = new Plane(plane);
        for (int y = 0; y < planeCopy.getHeight(); y++) {
            for (int x = 0; x < planeCopy.getWidth(); x++) {
                patchMaterial = planeCopy.getPixel(x, y);
                if (patchMaterial != Material.NONE) {
                    beginPatch(mergeMaterials ? null : patchMaterial);
                    todoX[0] = x;
                    todoY[0] = y;
                    todoCount = 1;
                    clearPatch();
                    finishPatch();
                }
            }
        }
    }

    private void clearPatch() {
        while (todoCount > 0) {
            todoCount--;
            int x = todoX[todoCount];
            int y = todoY[todoCount];
            handlePixel(x, y);
            planeCopy.setPixel(x, y, Material.NONE);
            checkPixel(x - 1, y);
            checkPixel(x + 1, y);
            checkPixel(x, y - 1);
            checkPixel(x, y + 1);
        }
    }

    private void checkPixel(int x, int y) {
        Material pixelMaterial = planeCopy.getPixelAutoclip(x, y);
        if (mergeMaterials ? (pixelMaterial != Material.NONE) : (pixelMaterial == patchMaterial)) {
            if (todoCount == todoX.length) {
                todoX = grow(todoX);
                todoY = grow(todoY);
            }
            todoX[todoCount] = x;
            todoY[todoCount] = y;
            todoCount++;
        }
    }

    private static int[] grow(int[] a) {
        int[] b = new int[2 * a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
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
