package name.martingeisse.chipdraw.pixel.operation.mouse;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.operation.DesignOperation;

/**
 *
 */
public interface MouseTool {

	Result onMousePressed(Design design, int x, int y, MouseButton button, boolean shift);

	Result onMouseMoved(Design design, int x, int y);

	Result onMouseReleased(Design design);

	enum MouseButton {
		LEFT, MIDDLE, RIGHT;
	}

	final class Result {

		private final DesignOperation operation;
		private final boolean merge;

		public Result(DesignOperation operation, boolean merge) {
			if (operation == null) {
				throw new IllegalArgumentException("operation cannot be null");
			}
			this.operation = operation;
			this.merge = merge;
		}

		public DesignOperation getOperation() {
			return operation;
		}

		public boolean isMerge() {
			return merge;
		}

	}

}
