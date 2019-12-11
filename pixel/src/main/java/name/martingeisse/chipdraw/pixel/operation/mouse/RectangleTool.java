package name.martingeisse.chipdraw.pixel.operation.mouse;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.operation.library.DrawPoints;
import name.martingeisse.chipdraw.pixel.operation.library.ErasePoints;

import java.awt.*;
import java.util.Random;
import java.util.function.Supplier;

/**
 *
 */
public class RectangleTool implements MouseTool {

	private final Supplier<Material> materialProvider;
	private boolean active, erasing;
	private int startX, startY;
	private int endX, endY;
	private int minX, minY, maxX, maxY;

	public RectangleTool(Supplier<Material> materialProvider) {
		this.materialProvider = materialProvider;
	}

	private void translateBounds() {
		minX = Math.min(startX, endX);
		minY = Math.min(startY, endY);
		maxX = Math.max(startX, endX);
		maxY = Math.max(startY, endY);
	}

	@Override
	public void draw(Graphics2D g, int zoom) {
		Random random = new Random();
		g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
		g.drawRect(minX * zoom, minY * zoom, (maxX + 1) * zoom, (maxY + 1) * zoom);
	}

	@Override
	public Result onMousePressed(Design design, int x, int y, MouseButton button, boolean shift) {
		active = true;
		erasing = (button == MouseButton.RIGHT);
		startX = endX = x;
		startY = endY = y;
		translateBounds();
		return null;
	}

	@Override
	public Result onMouseMoved(Design design, int x, int y) {
		endX = x;
		endY = y;
		translateBounds();
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
		if (erasing) {
			return new Result(new ErasePoints(minX, minY, maxX - minX + 1, maxY - minY + 1, materialProvider.get().getPlaneSchema()), false);
		} else {
			return new Result(new DrawPoints(minX, minY, maxX - minX + 1, maxY - minY + 1, materialProvider.get()), false);
		}
	}

}
