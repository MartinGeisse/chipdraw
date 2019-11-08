package name.martingeisse.chipdraw.global_tools;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Plane;

/**
 *
 */
public abstract class ConnectivityExtractor extends AbstractPerPlaneExtractor {

	private int localMaterialIndex = -1;

	@Override
	protected void handlePlane(Plane plane) {
		Plane copy = new Plane(plane);
		for (int y = 0; y < copy.getHeight(); y++) {
			for (int x = 0; x < copy.getWidth(); x++) {
				localMaterialIndex = copy.getPixel(x, y);
				if (localMaterialIndex != Plane.EMPTY_PIXEL) {
					beginPatch(localMaterialIndex);
					clear(copy, x, y);
					finishPatch();
				}
			}
		}
	}

	private void clear(Plane copy, int x, int y) {
		int localMaterialIndex = copy.getPixelAutoclip(x, y);
		if (localMaterialIndex != Plane.EMPTY_PIXEL && localMaterialIndex == this.localMaterialIndex) {
			handlePixel(x, y);
			copy.setPixel(x, y, Plane.EMPTY_PIXEL);
			clear(copy, x - 1, y);
			clear(copy, x + 1, y);
			clear(copy, x, y - 1);
			clear(copy, x, y + 1);
		}
	}

	protected void beginPatch(int localMaterialIndex) {
	}

	protected void handlePixel(int x, int y) {
	}

	protected void finishPatch() {
	}

	public static class Test extends ConnectivityExtractor {

		@Override
		protected void beginDesign(Design design) {
			System.out.println();
			System.out.println("*");
			System.out.println("* Connectivity extraction");
			System.out.println("*");
			System.out.println();
		}

		@Override
		protected boolean beginPlane(Plane plane) {
			System.out.println("* Plane");
			return true;
		}

		@Override
		protected void beginPatch(int localMaterialIndex) {
			System.out.println("found patch with local material index: " + localMaterialIndex);
		}

	}

}
