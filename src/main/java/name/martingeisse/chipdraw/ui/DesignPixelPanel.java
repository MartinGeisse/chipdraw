package name.martingeisse.chipdraw.ui;

import name.martingeisse.chipdraw.Design;
import name.martingeisse.chipdraw.MainWindow;

import javax.swing.*;
import java.awt.*;

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
				drawPixel(x, y, x * cellSize, y * cellSize, cellSize);
			}
		}

	}

	protected abstract void drawPixel(int cellX, int cellY, int screenX, int screenY, int screenSize);

}
