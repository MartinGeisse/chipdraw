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
		for (Plane plane : design.getLayers()) {
			System.out.println("* Plane");
			Plane copy = plane.createCopy();
			for (int y = 0; y < copy.getHeight(); y++) {
				for (int x = 0; x < copy.getWidth(); x++) {
					boolean layer = copy.getCell(x, y);
					if (layer) {
						System.out.println("found patch at " + x + ", " + y + ", layer " + layer);
						clear(copy, x, y, layer);
					}
				}
			}
		}
	}

	private static void clear(Plane copy, int x, int y, boolean layer) {
		if (copy.getCellAutoclip(x, y) == layer) {
			copy.setCell(x, y, false);
			clear(copy, x - 1, y, layer);
			clear(copy, x + 1, y, layer);
			clear(copy, x, y - 1, layer);
			clear(copy, x, y + 1, layer);
		}
	}

}
