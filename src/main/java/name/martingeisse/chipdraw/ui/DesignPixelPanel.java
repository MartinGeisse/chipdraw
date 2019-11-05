package name.martingeisse.chipdraw.ui;

import name.martingeisse.chipdraw.Design;
import name.martingeisse.chipdraw.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public abstract class DesignPixelPanel extends JPanel {

	private final MainWindow mainWindow;

	public DesignPixelPanel(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	@Override
	protected void paintComponent(Graphics _g) {
		Graphics2D g = (Graphics2D) _g;
		Design design = mainWindow.getCurrentDesign();

		// get width / height / cell size
		int cellSize = mainWindow.getCurrentCellSize();
		int panelWidth = getWidth();
		int panelHeight = getHeight();
		int designDisplayWidth = design.getWidth() * cellSize;
		int designDisplayHeight = design.getHeight() * cellSize;

		// draw background
		if (designDisplayWidth < panelWidth || designDisplayHeight < panelHeight) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, panelWidth, panelHeight);
		}
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, designDisplayWidth, designDisplayHeight);

		// draw pixels
		for (int x = 0; x < design.getWidth(); x++) {
			for (int y = 0; y < design.getHeight(); y++) {
				drawPixel(g, x, y, x * cellSize, y * cellSize, cellSize);
			}
		}

	}

	protected abstract void drawPixel(Graphics2D g, int cellX, int cellY, int screenX, int screenY, int screenSize);

	protected static Paint createHatching(int color) {
		BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, color);
		image.setRGB(1, 1, color);
		image.setRGB(2, 2, color);
		return new TexturePaint(image, new Rectangle2D.Float(0, 0, 3, 3));
	}

	protected static Paint createCrossHatching(int color) {
		BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, color);
		image.setRGB(2, 0, color);
		image.setRGB(1, 1, color);
		image.setRGB(0, 2, color);
		image.setRGB(2, 2, color);
		return new TexturePaint(image, new Rectangle2D.Float(0, 0, 3, 3));
	}

}
