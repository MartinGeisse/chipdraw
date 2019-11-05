package name.martingeisse.chipdraw.extractor;

import name.martingeisse.chipdraw.Design;
import name.martingeisse.chipdraw.Plane;

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
		for (Plane plane : design.getPlanes()) {
			System.out.println("* Plane");
			Plane copy = new Plane(plane);
			for (int y = 0; y < copy.getHeight(); y++) {
				for (int x = 0; x < copy.getWidth(); x++) {
					int localMaterialIndex = copy.getCell(x, y);
					if (localMaterialIndex != Plane.EMPTY_CELL) {
						extractRectangle(copy, x, y, localMaterialIndex);
					}
				}
			}
		}
	}

	private static void extractRectangle(Plane copy, int topLeftX, int topLeftY, int localMaterialIndex) {

		// determine width of the first row which is also the width of the rectangle
		int rectangleWidth = 1;
		while (topLeftX + rectangleWidth < copy.getWidth() && copy.getCell(topLeftX + rectangleWidth, topLeftY) == localMaterialIndex) {
			rectangleWidth++;
		}

		// determine the height of the rectangle by extending downwards
		int rectangleHeight = 1;
		while (topLeftY + rectangleHeight < copy.getHeight()) {

			// check if all extension pixels are set
			if (!copy.isReactangleUniformAutoclip(topLeftX, topLeftY + rectangleHeight, rectangleWidth, 1, localMaterialIndex)) {
				break;
			}

			// check that the extension row cannot be filled by a wider rectangle
			if (topLeftX > 0 && copy.getCell(topLeftX - 1, topLeftY + rectangleHeight) == localMaterialIndex) {
				break;
			}
			if (topLeftX + rectangleWidth < copy.getWidth() && copy.getCell(topLeftX + rectangleWidth, topLeftY + rectangleHeight) == localMaterialIndex) {
				break;
			}

			rectangleHeight++;
		}

		System.out.println("found rectangle: " + topLeftX + ", " + topLeftY + " / " + rectangleWidth + " x " + rectangleHeight + ", local material index " + localMaterialIndex);
		copy.drawRectangle(topLeftX, topLeftY, rectangleWidth, rectangleHeight, Plane.EMPTY_CELL);
	}

}
