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
		for (Plane plane : design.getPlanes()) {
			System.out.println("* Plane");
			Plane copy = new Plane(plane);
			for (int y = 0; y < copy.getHeight(); y++) {
				for (int x = 0; x < copy.getWidth(); x++) {
					int localMaterialIndex = copy.getCell(x, y);
					if (localMaterialIndex != Plane.EMPTY_CELL) {
						System.out.println("found patch at " + x + ", " + y + ", local material index: " + localMaterialIndex);
						clear(copy, x, y, localMaterialIndex);
					}
				}
			}
		}
	}

	private static void clear(Plane copy, int x, int y, int localMaterialIndex) {
		if (copy.getCellAutoclip(x, y) == localMaterialIndex) {
			copy.setCell(x, y, Plane.EMPTY_CELL);
			clear(copy, x - 1, y, localMaterialIndex);
			clear(copy, x + 1, y, localMaterialIndex);
			clear(copy, x, y - 1, localMaterialIndex);
			clear(copy, x, y + 1, localMaterialIndex);
		}
	}

}
