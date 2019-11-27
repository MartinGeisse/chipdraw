package name.martingeisse.chipdraw.pnr.cell;

/**
 *
 */
public interface CellSymbol {

	/**
	 * Coordinates are in the range 0..100; scaling, rotation and mirroring are done automatically by the context.
	 */
	void draw(DrawContext context);

	interface DrawContext {
		void drawLine(int x1, int y1, int x2, int y2);
		void drawCircle(int x, int y, int radius);
	}

}
