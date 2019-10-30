package name.martingeisse.chipdraw;

/**
 *
 */
public final class ConnectivityExtractor {

	// prevent instantiation
	private ConnectivityExtractor() {
	}

	public static void extract(Design design) {
		System.out.println();
		System.out.println("*");
		System.out.println("* Connectivity extraction");
		System.out.println("*");
		System.out.println();
		for (Layer layer : design.getLayers()) {
			System.out.println("* Layer");
			Layer copy = layer.createCopy();
			for (int y = 0; y < copy.getHeight(); y++) {
				for (int x = 0; x < copy.getWidth(); x++) {
					if (copy.getCell(x, y)) {
						System.out.println("found patch at " + x + ", " + y);
						clear(copy, x, y);
					}
				}
			}
		}
	}

	private static void clear(Layer copy, int x, int y) {
		if (copy.getCellAutoclip(x, y)) {
			copy.setCell(x, y, false);
			clear(copy, x - 1, y);
			clear(copy, x + 1, y);
			clear(copy, x, y - 1);
			clear(copy, x, y + 1);
		}
	}

}
