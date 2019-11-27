package name.martingeisse.chipdraw.pnr.ui;

import name.martingeisse.chipdraw.pnr.design.Design;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public abstract class DesignTilePanel extends JPanel {

	private final MainWindow mainWindow;

	public DesignTilePanel(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	@Override
	protected void paintComponent(Graphics _g) {
		Graphics2D g = (Graphics2D) _g;
		Design design = mainWindow.getCurrentDesign();

		// get width / height / pixel size
		int pixelSize = mainWindow.getCurrentPixelSize();
		int panelWidth = getWidth();
		int panelHeight = getHeight();
		int designDisplayWidth = design.getWidth() * pixelSize;
		int designDisplayHeight = design.getHeight() * pixelSize;

		// draw background
		if (designDisplayWidth < panelWidth || designDisplayHeight < panelHeight) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, panelWidth, panelHeight);
		}
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, designDisplayWidth, designDisplayHeight);

		// draw grid
		g.setColor(new Color(32, 32, 32));
		for (int x = 1; x < design.getWidth(); x++) {
			g.drawLine(x * pixelSize, 0, x * pixelSize, design.getHeight() * pixelSize);
		}
		for (int y = 1; y < design.getHeight(); y++) {
			g.drawLine(0, y * pixelSize, design.getWidth() * pixelSize, y * pixelSize);
		}

		// draw pixels
		for (int x = 0; x < design.getWidth(); x++) {
			for (int y = 0; y < design.getHeight(); y++) {
				drawPixel(g, x, y, x * pixelSize, y * pixelSize, pixelSize);
			}
		}

	}

	protected abstract void drawPixel(Graphics2D g, int pixelX, int pixelY, int screenX, int screenY, int screenSize);

}
