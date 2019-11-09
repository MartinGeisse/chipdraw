package name.martingeisse.chipdraw.operation.library;

/*
public final class DrawPoints extends InPlaceDesignOperation {

    private final int planeIndex;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public DrawPoints(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Response perform(Design design) throws UserVisibleMessageException {
        return new Response();
    }

    public static final class MyUndoer {

        private final Map<Point, Integer> originalPixels = new HashMap<>();

        MyUndoer(DrawPoints operation, Design design) {
            int x = operation.getX(), y = operation.getY();
            for (int i = 0; i < operation.width; i++) {
                for (int j = 0; j < operation.height; j++) {
                    originalPixels.put(new Point(x + i, y + j), design.)
                }
            }
        }

    }

}
*/