package name.martingeisse.chipdraw.pixel.operation.mouse;

import name.martingeisse.chipdraw.pixel.operation.library.DeleteRowOperation;
import name.martingeisse.chipdraw.pixel.operation.library.MultiplyRowOperation;

/**
 *
 */
public class RowTool implements MouseTool {

	@Override
	public Result onMousePressed(int x, int y, MouseButton button, boolean shift) {
		if (button == MouseButton.LEFT) {
			return new Result(new MultiplyRowOperation(y, shift ? 5 : 1), false);
		} else if (button == MouseButton.RIGHT) {
			return new Result(new DeleteRowOperation(y, shift ? 5 : 1), false);
		} else {
			return null;
		}
	}

	@Override
	public Result onMouseMoved(int x, int y) {
		return null;
	}

	@Override
	public Result onMouseReleased() {
		return null;
	}

}
