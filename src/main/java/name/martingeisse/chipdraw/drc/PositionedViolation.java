package name.martingeisse.chipdraw.drc;

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

    @Override
    public String getFullText() {
        return "at (" + x + ", " + y + "): " + super.getFullText();
    }

}
