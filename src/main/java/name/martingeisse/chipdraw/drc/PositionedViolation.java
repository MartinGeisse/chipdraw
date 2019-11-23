package name.martingeisse.chipdraw.drc;

import name.martingeisse.chipdraw.util.Point;

public class PositionedViolation extends Violation {

    private final int x, y;

    public PositionedViolation(String message, int x, int y) {
        super(message);
        this.x = x;
        this.y = y;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final Point getPoint() {
        return new Point(x, y);
    }

    @Override
    public String getFullText() {
        return "at (" + x + ", " + y + "): " + super.getFullText();
    }

}
