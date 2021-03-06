package name.martingeisse.chipdraw.pixel.global_tools;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;

/**
 *
 */
public abstract class CornerStitchingExtrator extends AbstractPerPlaneExtractor {

	private Material material = null;

	@Override
	protected void handlePlane(Plane plane) {
		Plane copy = new Plane(plane);
		for (int y = 0; y < copy.getHeight(); y++) {
			for (int x = 0; x < copy.getWidth(); x++) {
				material = copy.getPixel(x, y);
				if (material != Material.NONE) {
					extractRectangle(copy, x, y);
				}
			}
		}
	}

	private void extractRectangle(Plane copy, int topLeftX, int topLeftY) {
		beginRectangle(material);

		// determine width of the first row which is also the width of the rectangle
		int rectangleWidth = 1;
		while (topLeftX + rectangleWidth < copy.getWidth() && copy.getPixel(topLeftX + rectangleWidth, topLeftY) == material) {
			rectangleWidth++;
		}

		// determine the height of the rectangle by extending downwards
		int rectangleHeight = 1;
		while (topLeftY + rectangleHeight < copy.getHeight()) {

			// check if all extension pixels are set
			if (!copy.isRectangleUniform(topLeftX, topLeftY + rectangleHeight, rectangleWidth, 1, material)) {
				break;
			}

			// check that the extension row cannot be filled by a wider rectangle
			if (topLeftX > 0 && copy.getPixel(topLeftX - 1, topLeftY + rectangleHeight) == material) {
				break;
			}
			if (topLeftX + rectangleWidth < copy.getWidth() && copy.getPixel(topLeftX + rectangleWidth, topLeftY + rectangleHeight) == material) {
				break;
			}

			rectangleHeight++;
		}

		finishRectangle(material, topLeftX, topLeftY, rectangleWidth, rectangleHeight);
		copy.drawRectangle(topLeftX, topLeftY, rectangleWidth, rectangleHeight, Material.NONE);
	}

	protected void beginRectangle(Material material) {
	}

	protected void finishRectangle(Material material, int x, int y, int width, int height) {
	}

	public static class Test extends CornerStitchingExtrator {

		@Override
		protected void beginDesign(Design design) {
			System.out.println();
			System.out.println("*");
			System.out.println("* Corner-stitching extraction");
			System.out.println("*");
			System.out.println();
		}

		@Override
		protected boolean beginPlane(Plane plane) {
			System.out.println("* Plane");
			return true;
		}

		@Override
		protected void finishRectangle(Material material, int x, int y, int width, int height) {
			System.out.println("found rectangle: " + x + ", " + y + " / " + width + " x " + height +
					", local material index " + material);
		}

	}

}
