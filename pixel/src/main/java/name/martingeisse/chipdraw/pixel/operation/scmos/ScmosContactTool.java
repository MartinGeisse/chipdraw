package name.martingeisse.chipdraw.pixel.operation.scmos;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.operation.DesignOperation;
import name.martingeisse.chipdraw.pixel.operation.mouse.AbstractClickTool;

import java.awt.*;
import java.util.Random;

/**
 *
 */
public final class ScmosContactTool extends AbstractClickTool {

	private final ScmosContactType contactType;

	public ScmosContactTool(ScmosContactType contactType) {
		this.contactType = contactType;
	}

	@Override
	protected DesignOperation onClick(Design design, int x, int y, MouseButton button, boolean shift) {
		if (button == MouseButton.LEFT) {
			return new ScmosContactOperation(contactType, x, y);
		}
		return null;
	}

	@Override
	public void draw(Graphics2D g, int zoom, boolean shift) {
		Random random = new Random();
		g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
		int x = getMouseX();
		int y = getMouseY();
		g.drawRect(x * zoom, y * zoom, 6 * zoom, 6 * zoom);
		g.drawRect((x + 1) * zoom, (y + 1) * zoom, 4 * zoom, 4 * zoom);
		g.drawRect((x + 2) * zoom, (y + 2) * zoom, 2 * zoom, 2 * zoom);
	}

}
