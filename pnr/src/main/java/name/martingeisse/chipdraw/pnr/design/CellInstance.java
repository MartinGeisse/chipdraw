package name.martingeisse.chipdraw.pnr.design;

import name.martingeisse.chipdraw.pnr.cell.CellTemplate;

/**
 * TODO: flip, rotate
 */
public final class CellInstance {

    private final CellTemplate template;
    private final int x, y;

    public CellInstance(CellTemplate template, int x, int y) {
        this.template = template;
        this.x = x;
        this.y = y;
    }

    public CellTemplate getTemplate() {
        return template;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean overlaps(int pointX, int pointY) {
		return (pointX >= x && pointY >= y && pointX < x + template.getWidth() && pointY < y + template.getHeight());
    }

    public boolean overlaps(CellInstance other) {
        int right = x + template.getWidth();
        int bottom = y + template.getHeight();
        int otherRight = other.getX() + other.getTemplate().getWidth();
        int otherBottom = other.getY() + other.getTemplate().getHeight();
        // TODO check
        return x < otherRight && other.getX() < right && y < otherBottom && other.getY() < bottom;
    }

}
