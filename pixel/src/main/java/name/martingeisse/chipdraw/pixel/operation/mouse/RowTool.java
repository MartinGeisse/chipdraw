package name.martingeisse.chipdraw.pixel.operation.mouse;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.operation.library.DeleteRowOperation;
import name.martingeisse.chipdraw.pixel.operation.library.MultiplyRowOperation;

/**
 *
 */
public class RowTool implements MouseTool {

	@Override
	public Result onMousePressed(Design design, int x, int y, MouseButton button, boolean shift) {
		if (y >= design.getHeight()) {
			return null;
		} else if (button == MouseButton.LEFT) {
			return new Result(new MultiplyRowOperation(y, shift ? 6 : 2), false);
		} else if (button == MouseButton.RIGHT) {
			return new Result(new DeleteRowOperation(y, shift ? 5 : 1), false);
		} else {
			return null;
		}
	}

	@Override
	public Result onMouseMoved(Design design, int x, int y) {
		return null;
	}

	@Override
	public Result onMouseReleased(Design design) {
		return null;
	}

}
