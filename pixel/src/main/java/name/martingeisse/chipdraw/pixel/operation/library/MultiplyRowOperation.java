package name.martingeisse.chipdraw.pixel.operation.library;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.operation.OutOfPlaceDesignOperation;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

/**
 *
 */
public class MultiplyRowOperation extends OutOfPlaceDesignOperation {

	private final int rowIndex;
	private final int multiplier;

	public MultiplyRowOperation(int rowIndex, int multiplier) {
		if (rowIndex < 0) {
			throw new IllegalArgumentException("rowIndex cannot be negative");
		}
		if (multiplier < 0) {
			throw new IllegalArgumentException("multiplier cannot be negative");
		}
		this.rowIndex = rowIndex;
		this.multiplier = multiplier;
	}

	@Override
	protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
		if (rowIndex >= oldDesign.getHeight()) {
			throw new RuntimeException("invalid rowIndex " + rowIndex + " for design height " + oldDesign.getHeight());
		}
		Design newDesign = new Design(oldDesign.getTechnology(), oldDesign.getWidth(), oldDesign.getHeight() - 1 + multiplier);
		newDesign.copyFrom(oldDesign, 0, 0, 0, 0, oldDesign.getWidth(), rowIndex);
		for (int i = 0; i < multiplier; i++) {
			newDesign.copyFrom(oldDesign, 0, rowIndex, 0, rowIndex + i, oldDesign.getWidth(), 1);
		}
		newDesign.copyFrom(oldDesign, 0, rowIndex + 1, 0, rowIndex + multiplier, oldDesign.getWidth(), oldDesign.getHeight() - rowIndex - 1);
		return newDesign;
	}

}
