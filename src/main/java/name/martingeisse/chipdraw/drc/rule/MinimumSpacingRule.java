package name.martingeisse.chipdraw.drc.rule;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.drc.DrcContext;
import name.martingeisse.chipdraw.global_tools.ConnectivityExtractor;
import name.martingeisse.chipdraw.util.Point;

import java.util.HashSet;
import java.util.Set;

/**
 * Splits the target plane into connected "patches" and ensures that a minimum spacing between patches is obeyed.
 */
public class MinimumSpacingRule implements Rule {

    private final PlaneSchema planeSchema;
    private final MaterialMode materialMode;
    private final int spacing;
    private DrcContext context;
    private Plane plane;

    public MinimumSpacingRule(PlaneSchema planeSchema, MaterialMode materialMode, int spacing) {
        this.planeSchema = planeSchema;
        this.materialMode = materialMode;
        this.spacing = spacing;
    }

    @Override
    public void check(DrcContext context) {
        this.context = context;
        this.plane = context.getDesign().getPlane(planeSchema);
        new MyConnectivityExtractor().extract(plane);
    }

    private class MyConnectivityExtractor extends ConnectivityExtractor {

        private final Set<Point> points = new HashSet<>();
        private Material material;

        public MyConnectivityExtractor() {
            super(materialMode == MaterialMode.MERGE_MATERIALS);
        }

        @Override
        protected void beginPatch(Material material) {
            this.points.clear();
            this.material = material;
        }

        @Override
        protected void handlePixel(int x, int y) {
            points.add(new Point(x, y));
        }

        @Override
        protected void finishPatch() {
            for (Point point : points) {
                int x = point.getX();
                int y = point.getY();
                for (int dx = -spacing; dx <= spacing; dx++) {
                    for (int dy = -spacing; dy <= spacing; dy++) {
                        int xdx = x + dx;
                        int ydy = y + dy;
                        Point otherPoint = new Point(xdx, ydy);
                        Material otherMaterial = plane.getPixel(xdx, ydy);
                        if (otherMaterial != Material.NONE && !points.contains(otherPoint)) {
                            if (otherMaterial == material || materialMode != MaterialMode.IGNORE_OTHER_MATERIALS) {
                                context.report(x, y, "plane " + planeSchema + " violates minimum spacing of " + spacing);
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Specifies how different materials in the plane being checked behave towards each other.
     */
    public enum MaterialMode {

        /**
         * All materials are considered equal (e.g. metal and via). This means that a "patch" may consist of multiple
         * different but connected materials and must obey spacing rules with respect to other patches.
         */
        MERGE_MATERIALS,

        /**
         * Materials are considered different and they all must obey spacing rules with repsect to patches of any
         * material. A patch cannot consist of more than one material, and trying to do so leaves a spacing of 0.
         */
        CHECK_OTHER_MATERIAL_SPACING,

        /**
         * A patch cannot consist of more than one material, but each patch must obey spacing rules only with respect
         * to patches of the same material. Trying to build a patch of more than one material creates several patches,
         * but their spacing towards each other is not checked.
         */
        IGNORE_OTHER_MATERIALS

    }
}
