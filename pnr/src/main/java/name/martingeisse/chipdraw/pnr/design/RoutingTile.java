package name.martingeisse.chipdraw.pnr.design;

/**
 * A routing tile specifies connections along the three axes. Each tile only specifies connections to the east, south
 * and downwards; a connection, for example, to the west is specified as a connection to the east by its west neighbor.
 *
 * A slight irregularity exists that should simplify the code: The east and south directions point towards the positive
 * x- and y-axes, while the down direction points towards the *negative* z-axis (plane index). The reason for this is
 * that we need the down direction, not up, to connect to the cells which do not make connections on their own; and
 * if we stored west and north (negative x- and y-axes) instead of east and south, we would have to add a dummy border
 * of one tile or shift all x- and y-indices by 1.
 */
public enum RoutingTile {

    NONE(false, false, false),

    EAST(true, false, false),

    SOUTH(false, true, false),

    SOUTH_EAST(true, true, false),

    DOWN(false, false, true),

    DOWN_EAST(true, false, true),

    DOWN_SOUTH(false, true, true),

    DOWN_SOUTH_EAST(true, true, true);

    private final boolean eastConnected;
    private final boolean southConnected;
    private final boolean downConnected;

    RoutingTile(boolean eastConnected, boolean southConnected, boolean downConnected) {
        this.eastConnected = eastConnected;
        this.southConnected = southConnected;
        this.downConnected = downConnected;
    }

    public boolean isEastConnected() {
        return eastConnected;
    }

    public boolean isSouthConnected() {
        return southConnected;
    }

    public boolean isDownConnected() {
        return downConnected;
    }

    byte getCode() {
        return (byte)ordinal();
    }

    static RoutingTile getForCode(byte code) {
        if (code < 0 || code > 7) {
            throw new IllegalArgumentException("invalid routing tile code: " + code);
        }
        return values()[code];
    }

}
