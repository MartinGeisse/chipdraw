package name.martingeisse.chipdraw.pixel.operation.mouse;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.operation.DesignOperation;
import name.martingeisse.chipdraw.pixel.operation.library.DrawPoints;
import name.martingeisse.chipdraw.pixel.operation.library.ErasePoints;

import java.util.function.Supplier;

/**
 *
 */
public final class DrawTool implements MouseTool {

	private final Supplier<Material> materialProvider;
	private final int cursorSize;
	private boolean drawing, erasing, firstPixelOfStroke;

	public DrawTool(Supplier<Material> materialProvider, int cursorSize) {
		this.materialProvider = materialProvider;
		this.cursorSize = cursorSize;
	}

	@Override
	public Result onMousePressed(Design design, int x, int y, MouseButton button, boolean shift) {
		drawing = (button == MouseButton.LEFT);
		erasing = (button == MouseButton.RIGHT);
		firstPixelOfStroke = true;
		return onMouseMoved(design, x, y);
	}

	@Override
	public Result onMouseMoved(Design design, int x, int y) {
		DesignOperation operation = null;
		boolean merge = !firstPixelOfStroke;
		if (drawing || erasing) {
			int offset = (cursorSize - 1) / 2;
			Material material = materialProvider.get();
			if (drawing) {
				operation = new DrawPoints(x - offset, y - offset, cursorSize, cursorSize, material);
			} else {
				operation = new ErasePoints(x - offset, y - offset, cursorSize, cursorSize, material.getPlaneSchema());
			}
			firstPixelOfStroke = false;
		}
		return (operation == null ? null : new Result(operation, merge));
	}

	@Override
	public Result onMouseReleased(Design design) {
		drawing = erasing = false;
		return null;
	}

}
