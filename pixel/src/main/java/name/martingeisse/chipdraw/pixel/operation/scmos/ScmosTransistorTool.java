package name.martingeisse.chipdraw.pixel.operation.scmos;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.operation.DesignOperation;
import name.martingeisse.chipdraw.pixel.operation.mouse.AbstractClickTool;

import java.awt.*;
import java.util.Random;

/**
 *
 */
public final class ScmosTransistorTool extends AbstractClickTool {

	private final Material sourceDrainMaterial;

	public ScmosTransistorTool(Material sourceDrainMaterial) {
		this.sourceDrainMaterial = sourceDrainMaterial;
	}

	@Override
	protected DesignOperation onClick(Design design, int x, int y, MouseButton button, boolean shift) {
		if (button == MouseButton.LEFT) {
			return new ScmosTransistorOperation(sourceDrainMaterial, x, y, shift);
		}
		return null;
	}

	@Override
	public void draw(Graphics2D g, int zoom) {
		Random random = new Random();
		g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
		int x = getMouseX();
		int y = getMouseY();
		g.drawRect(x * zoom, y * zoom, 6 * zoom, 6 * zoom);
		g.drawRect((x + 2) * zoom, (y - 2) * zoom, 2 * zoom, 10 * zoom);
		// TODO draw horizontally when shift is pressed
	}

}
