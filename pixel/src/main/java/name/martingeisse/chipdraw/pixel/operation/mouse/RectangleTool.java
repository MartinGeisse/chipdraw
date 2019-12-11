package name.martingeisse.chipdraw.pixel.operation.mouse;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.operation.library.DrawPoints;

import java.util.function.Supplier;

/**
 *
 */
public class RectangleTool implements MouseTool {

	private final Supplier<Material> materialProvider;
	private boolean active;
	private int startX, startY;
	private int endX, endY;

	public RectangleTool(Supplier<Material> materialProvider) {
		this.materialProvider = materialProvider;
	}

	@Override
	public Result onMousePressed(Design design, int x, int y, MouseButton button, boolean shift) {
		active = true;
		startX = x;
		startY = y;
		return null;
	}

	@Override
	public Result onMouseMoved(Design design, int x, int y) {
		endX = x;
		endY = y;
		return null;
	}

	@Override
	public Result onMouseReleased(Design design) {
		active = false;
		if (startX > endX) {
			int temp = startX;
			startX = endX;
			endX = temp;
		}
		if (startY > endY) {
			int temp = startY;
			startY = endY;
			endY = temp;
		}
		return new Result(new DrawPoints(startX, startY, endX - startX + 1, endY - startY + 1, materialProvider.get()), false);
	}

}
