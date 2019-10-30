package name.martingeisse.chipdraw;

/**
 *
 */
public final class CornerStitchingExtrator {

	// prevent instantiation
	private CornerStitchingExtrator() {
	}

	public static void extract(Design design) {
		System.out.println();
		System.out.println("*");
		System.out.println("* Corner-stitching extraction");
		System.out.println("*");
		System.out.println();
		for (Layer layer : design.getLayers()) {
			System.out.println("* Layer");
			Layer copy = layer.createCopy();
			for (int x = 0; x < copy.getWidth(); x++) {
				for (int y = 0; y < copy.getHeight(); y++) {
					if (layer.getCell(x, y)) {
						extractRectangle(copy, x, y);
					}
				}
			}
		}
	}

	// TODO refactor until readable!
	private static void extractRectangle(Layer copy, int topLeftX, int topLeftY) {

		// determine width of the first row which is also the width of the rectangle
		int rectangleWidth = 1;
		while (topLeftX + rectangleWidth < copy.getWidth() && copy.getCell(topLeftX + rectangleWidth, topLeftY)) {
			rectangleWidth++;
		}

		// determine the height of the rectangle by extending downwards
		int rectangleHeight = 1;
		extendHeight: while (topLeftY + rectangleHeight < copy.getHeight()) {

			// check if all extension pixels are set
			for (int x = topLeftX; x < topLeftX + rectangleWidth; x++) {
				if (!copy.getCell(x, topLeftY + rectangleHeight)) {
					break extendHeight;
				}
			}

			// check that the extension row cannot be filled by a wider rectangle
			if (topLeftX > 0 && copy.getCell(topLeftX - 1, topLeftY + rectangleHeight)) {
				break;
			}
			if (topLeftX + rectangleWidth < copy.getWidth() && copy.getCell(topLeftX + rectangleWidth, topLeftY + rectangleHeight)) {
				break;
			}

			rectangleHeight++;
		}

		System.out.println("found rectangle: " + topLeftX + ", " + topLeftY + " / " + rectangleWidth + " x " + rectangleHeight);
		copy.drawRectangle(topLeftX, topLeftY, rectangleWidth, rectangleHeight, false);
	}

}
