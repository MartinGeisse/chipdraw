package name.martingeisse.chipdraw.pixel.operation.library;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.operation.OutOfPlaceDesignOperation;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

/**
 *
 */
public class DeleteRowOperation extends OutOfPlaceDesignOperation {

	private final int rowIndex;
	private final int maxIdenticalRowCount;

	public DeleteRowOperation(int rowIndex) {
		this(rowIndex, 1);
	}

	public DeleteRowOperation(int rowIndex, int maxIdenticalRowCount) {
		if (rowIndex < 0) {
			throw new IllegalArgumentException("rowIndex cannot be negative");
		}
		if (maxIdenticalRowCount < 1) {
			throw new IllegalArgumentException("maxIdenticalRowCount must be positive");
		}
		this.rowIndex = rowIndex;
		this.maxIdenticalRowCount = maxIdenticalRowCount;
	}

	@Override
	protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
		if (rowIndex >= oldDesign.getHeight()) {
			throw new RuntimeException("invalid rowIndex " + rowIndex + " for design height " + oldDesign.getHeight());
		}
		int startIndex = rowIndex, endIndex = rowIndex + 1;
		while (startIndex > 0 && endIndex - startIndex < maxIdenticalRowCount && rowsIdentical(oldDesign, startIndex - 1, startIndex)) {
			startIndex--;
		}
		while (endIndex < oldDesign.getHeight() && endIndex - startIndex < maxIdenticalRowCount && rowsIdentical(oldDesign, endIndex, endIndex - 1)) {
			endIndex++;
		}
		int newHeight = startIndex + oldDesign.getHeight() - endIndex;
		if (newHeight == 0) {
			throw new UserVisibleMessageException("cannot delete all remaining rows");
		}
		Design newDesign = new Design(oldDesign.getTechnology(), oldDesign.getWidth(), newHeight);
		newDesign.copyFrom(oldDesign, 0, 0, 0, 0, oldDesign.getWidth(), startIndex);
		newDesign.copyFrom(oldDesign, 0, endIndex, 0, startIndex, oldDesign.getWidth(), oldDesign.getHeight() - endIndex);
		return newDesign;
	}

	private static boolean rowsIdentical(Design design, int rowIndex1, int rowIndex2) {
		for (Plane plane : design.getPlanes()) {
			for (int x = 0; x < design.getWidth(); x++) {
				if (plane.getPixel(x, rowIndex1) != plane.getPixel(x, rowIndex2)) {
					return false;
				}
			}
		}
		return true;
	}

}
