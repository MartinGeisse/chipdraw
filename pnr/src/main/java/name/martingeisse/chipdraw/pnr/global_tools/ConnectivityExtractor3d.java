package name.martingeisse.chipdraw.pnr.global_tools;

import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.design.RoutingPlane;
import name.martingeisse.chipdraw.pnr.design.RoutingTile;

/**
 * Extracts connected nets. A single net may span multiple planes and thus contain parts which are unconnected within
 * a plane.
 */
public abstract class ConnectivityExtractor3d {

    private RoutingPlane[] todoPlane = new RoutingPlane[100];
    private int[] todoX = new int[100];
    private int[] todoY = new int[100];
    private int todoCount;

    public final void extract(Design design) {
        design = new Design(design);
        for (RoutingPlane routingPlane : design.getRoutingPlanes()) {
            for (int x = 0; x < design.getWidth(); x++) {
                for (int y = 0; y < design.getHeight(); y++) {
                    // While a NONE tile may be connected to a net, we can ignore it here because that net must have
                    // at least one non-NONE tile which we will find instead.
                    if (routingPlane.getTile(x, y) != RoutingTile.NONE) {
                        beginPatch(routingPlane.getIndex(), x, y);
                        todoPlane[0] = routingPlane;
                        todoX[0] = x;
                        todoY[0] = y;
                        todoCount = 1;
                        clearPatch();
                        finishPatch();
                    }
                }
            }
        }
    }

    private void clearPatch() {
        while (todoCount > 0) {
            todoCount--;
            RoutingPlane plane = todoPlane[todoCount];
            int x = todoX[todoCount];
            int y = todoY[todoCount];
            handleTile(plane.getIndex(), x, y);
            RoutingTile tile = plane.getTile(x, y);
            if (tile.isEastConnected()) {
                addTodo(plane, x + 1, y);
            }
            if (x > 0 && plane.getTile(x - 1, y).isEastConnected()) {
                addTodo(plane, x - 1, y);
            }
            if (tile.isSouthConnected()) {
                addTodo(plane, x, y + 1);
            }
            if (y > 0 && plane.getTile(x, y - 1).isSouthConnected()) {
                addTodo(plane, x, y - 1);
            }
            if (tile.isDownConnected()) {
                RoutingPlane planeBelow = plane.getRoutingPlaneBelow();
                if (planeBelow == null) {
                    handleCellContact(x, y);
                } else {
                    addTodo(planeBelow, x, y);
                }
            }
            {
                RoutingPlane planeAbove = plane.getRoutingPlaneAbove();
                if (planeAbove != null && planeAbove.getTile(x, y).isDownConnected()) {
                    addTodo(planeAbove, x, y);
                }
            }
            plane.setTile(x, y, RoutingTile.NONE);
        }
    }

    private void addTodo(RoutingPlane plane, int x, int y) {
        if (todoCount == todoX.length) {
            todoPlane = grow(todoPlane);
            todoX = grow(todoX);
            todoY = grow(todoY);
        }
        todoPlane[todoCount] = plane;
        todoX[todoCount] = x;
        todoY[todoCount] = y;
        todoCount++;
    }

    private static RoutingPlane[] grow(RoutingPlane[] a) {
        RoutingPlane[] b = new RoutingPlane[2 * a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    private static int[] grow(int[] a) {
        int[] b = new int[2 * a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    protected void beginPatch(int planeIndex, int x, int y) {
    }

    protected void handleTile(int planeIndex, int x, int y) {
    }

    protected void handleCellContact(int x, int y) {
    }

    protected void finishPatch() {
    }

}
