package name.martingeisse.chipdraw.ui.util;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.ui.MainWindow;

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

		// draw grid
		g.setColor(Color.DARK_GRAY);
		for (int x = 1; x < design.getWidth(); x++) {
			g.drawLine(x * cellSize, 0, x * cellSize, design.getHeight() * cellSize);
		}
		for (int y = 1; y < design.getHeight(); y++) {
			g.drawLine(0, y * cellSize, design.getWidth() * cellSize, y * cellSize);
		}

	}

	protected abstract void drawPixel(Graphics2D g, int cellX, int cellY, int screenX, int screenY, int screenSize);

	protected static Paint createHatching(int color) {
		int size = 5;
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < size; i++) {
			image.setRGB(i, i, color);
		}
		return new TexturePaint(image, new Rectangle2D.Float(0, 0, size, size));
	}

	protected static Paint createCrossHatching(int color) {
		int size = 5;
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < size; i++) {
			image.setRGB(i, i, color);
			image.setRGB(size - 1 - i, i, color);
		}
		return new TexturePaint(image, new Rectangle2D.Float(0, 0, size, size));
	}

}
