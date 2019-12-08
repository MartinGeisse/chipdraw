package name.martingeisse.chipdraw.pixel.ui.util;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.ui.MainWindow;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class DesignPixelPanel extends JPanel {

    private static final int MACRO_GRID_SIZE = 7;

    private final MainWindow mainWindow;
    private final Map<Pair<Integer, Integer>, Paint> paintCache = new HashMap<>();

    public DesignPixelPanel(MainWindow mainWindow) {
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

        // draw pixels
        for (int x = 0; x < design.getWidth(); x++) {
            for (int y = 0; y < design.getHeight(); y++) {
                drawPixel(g, x, y, x * pixelSize, y * pixelSize, pixelSize);
            }
        }

        // draw grid
        g.setColor(Color.DARK_GRAY);
        for (int x = 1; x < design.getWidth(); x++) {
            g.drawLine(x * pixelSize, 0, x * pixelSize, design.getHeight() * pixelSize);
        }
        for (int y = 1; y < design.getHeight(); y++) {
            g.drawLine(0, y * pixelSize, design.getWidth() * pixelSize, y * pixelSize);
        }
        g.setColor(Color.GRAY);
        for (int x = MACRO_GRID_SIZE; x < design.getWidth(); x += MACRO_GRID_SIZE) {
            g.drawLine(x * pixelSize, 0, x * pixelSize, design.getHeight() * pixelSize);
        }
        for (int y = MACRO_GRID_SIZE; y < design.getHeight(); y += MACRO_GRID_SIZE) {
            g.drawLine(0, y * pixelSize, design.getWidth() * pixelSize, y * pixelSize);
        }

    }

    protected abstract void drawPixel(Graphics2D g, int pixelX, int pixelY, int screenX, int screenY, int screenSize);

    // caution: "dense" parameter is not part of the cache key!
    protected Paint getHatching(int color, int offset) {
        return getHatching(color, offset, false);
    }

    // caution: "dense" parameter is not part of the cache key!
    protected Paint getHatching(int color, int offset, boolean dense) {
        return paintCache.computeIfAbsent(Pair.of(color, offset), _ignored -> {
            int colorAndAlpha = color | 0xff000000;
            int size = dense ? 3 : 5;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < size; i++) {
                image.setRGB((i + offset) % size, i, colorAndAlpha);
            }
            return new TexturePaint(image, new Rectangle2D.Float(0, 0, size, size));
        });
    }

}
