package name.martingeisse.chipdraw.pixel.drc.rule;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.drc.DrcContext;
import name.martingeisse.chipdraw.pixel.global_tools.ConnectivityExtractor;
import name.martingeisse.chipdraw.pixel.util.Point;

import java.util.HashSet;
import java.util.Set;

/**
 * Ensures that connected groups of a specific material are shaped as exact NxN sized squares. This is used to
 * check the size of contacts and vias.
 */
public final class ExactMaterialSizeRule extends AbstractRule {

    private final Material material;
    private final int size;
    private DrcContext context;
    private Plane plane;

    public ExactMaterialSizeRule(Material material, int size) {
        this.material = material;
        this.size = size;
    }

    @Override
    public void check(DrcContext context) {
        this.context = context;
        plane = context.getDesign().getPlane(material.getPlaneSchema());
        new MyConnectivityExtractor().extract(plane);
    }

    private void checkShape(Set<Point> points) {
        if (!hasCorrectShape(points)) {
            Point point = points.iterator().next();
            context.report(point.getX(), point.getY(), getEffectiveErrorMessage());
        }
    }

    private boolean hasCorrectShape(Set<Point> points) {
        if (points.size() != size * size) {
            return false;
        }
        Point anchor = points.iterator().next();
        while (true) {
            Point neighbor = new Point(anchor.getX() - 1, anchor.getY());
            if (!points.contains(neighbor)) {
                break;
            }
            anchor = neighbor;
        }
        while (true) {
            Point neighbor = new Point(anchor.getX(), anchor.getY() - 1);
            if (!points.contains(neighbor)) {
                break;
            }
            anchor = neighbor;
        }
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                Point p = new Point(anchor.getX() + dx, anchor.getY() + dy);
                if (!points.contains(p)) {
                    return false;
                }
            }
        }
        return true;
    }

    private class MyConnectivityExtractor extends ConnectivityExtractor {

        private final Set<Point> points = new HashSet<>();
        private Material material;

        public MyConnectivityExtractor() {
            super(false);
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
            if (material == ExactMaterialSizeRule.this.material) {
                checkShape(points);
            }
        }

    }

}
