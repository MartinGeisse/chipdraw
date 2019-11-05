package name.martingeisse.chipdraw.extractor;

import name.martingeisse.chipdraw.Design;
import name.martingeisse.chipdraw.Plane;

/**
 *
 */
public abstract class ConnectivityExtractor extends AbstractPerPlaneExtractor {

	@Override
	protected void handlePlane(Plane plane) {
		Plane copy = new Plane(plane);
		for (int y = 0; y < copy.getHeight(); y++) {
			for (int x = 0; x < copy.getWidth(); x++) {
				int localMaterialIndex = copy.getCell(x, y);
				if (localMaterialIndex != Plane.EMPTY_CELL && handleFirst(x, y, localMaterialIndex)) {
					clear(copy, x, y);
				}
			}
		}
	}

	private void clear(Plane copy, int x, int y) {
		int localMaterialIndex = copy.getCellAutoclip(x, y);
		if (localMaterialIndex != Plane.EMPTY_CELL && handleNext(x, y, localMaterialIndex)) {
			copy.setCell(x, y, Plane.EMPTY_CELL);
			clear(copy, x - 1, y);
			clear(copy, x + 1, y);
			clear(copy, x, y - 1);
			clear(copy, x, y + 1);
		}
	}

	protected abstract boolean handleFirst(int x, int y, int localMaterialIndex);

	protected abstract boolean handleNext(int x, int y, int localMaterialIndex);

	public static class Uniform extends ConnectivityExtractor {

		private int localMaterialIndex = -1;

		@Override
		protected boolean handleFirst(int x, int y, int localMaterialIndex) {
			this.localMaterialIndex = localMaterialIndex;
			return true;
		}

		@Override
		protected boolean handleNext(int x, int y, int localMaterialIndex) {
			return localMaterialIndex == this.localMaterialIndex;
		}

	}

	public static class Test extends Uniform {

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
		protected boolean handleFirst(int x, int y, int localMaterialIndex) {
			boolean result = super.handleFirst(x, y, localMaterialIndex);
			if (result) {
				System.out.println("found patch at " + x + ", " + y + ", local material index: " + localMaterialIndex);
			}
			return result;
		}

	}

}
