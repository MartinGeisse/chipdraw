package name.martingeisse.chipdraw.pixel.ui.util;

import name.martingeisse.chipdraw.pixel.design.Design;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * This class performs painting the design so it can be done outside a component.
 */
public abstract class DesignPainter {

    private static final int MACRO_GRID_SIZE = 7;

    private final Map<Pair<Integer, Integer>, Paint> paintCache = new HashMap<>();

    public void paintDesign(Graphics2D g, Design design, int zoom) {

        // get width / height / pixel size
        int designDisplayWidth = design.getWidth() * zoom;
        int designDisplayHeight = design.getHeight() * zoom;

        // draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, designDisplayWidth, designDisplayHeight);

        // draw pixels
        for (int x = 0; x < design.getWidth(); x++) {
            for (int y = 0; y < design.getHeight(); y++) {
                drawPixel(g, x, y, x * zoom, y * zoom, zoom);
            }
        }

        // draw grid
        g.setColor(Color.DARK_GRAY);
        for (int x = 1; x < design.getWidth(); x++) {
            g.drawLine(x * zoom, 0, x * zoom, design.getHeight() * zoom);
        }
        for (int y = 1; y < design.getHeight(); y++) {
            g.drawLine(0, y * zoom, design.getWidth() * zoom, y * zoom);
        }
        g.setColor(Color.GRAY);
        for (int x = MACRO_GRID_SIZE; x < design.getWidth(); x += MACRO_GRID_SIZE) {
            g.drawLine(x * zoom, 0, x * zoom, design.getHeight() * zoom);
        }
        for (int y = MACRO_GRID_SIZE; y < design.getHeight(); y += MACRO_GRID_SIZE) {
            g.drawLine(0, y * zoom, design.getWidth() * zoom, y * zoom);
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
