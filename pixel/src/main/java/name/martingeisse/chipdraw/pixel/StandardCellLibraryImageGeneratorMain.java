package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.*;
import name.martingeisse.chipdraw.pixel.global_tools.magic.MagicFileIo;
import name.martingeisse.chipdraw.pixel.ui.util.DesignPainter;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class StandardCellLibraryImageGeneratorMain {

    private static final int ZOOM = 10;

    public static void main(String[] args) throws Exception {
        File magFolder = new File("resource/cell-lib/v2");
        File pngFolder = new File("../chipdraw.wiki");

        for (File pngFile : pngFolder.listFiles((ignored, name) -> name.endsWith(".png"))) {
            pngFile.delete();
        }

        File[] magFiles = magFolder.listFiles((ignored, name) -> name.endsWith(".mag"));
        Arrays.sort(magFiles);

        StringBuilder markdownPageBuilder = new StringBuilder();
        for (File magFile : magFiles) {
            String baseName = magFile.getName().substring(0, magFile.getName().length() - 4);
            File pngFile = new File(pngFolder, baseName + ".png");
            generateImage(magFile, pngFile);
            markdownPageBuilder.append("[" + baseName + "](" + pngFile.getName() + ")\n\n");
        }
        FileUtils.writeStringToFile(new File(pngFolder, "Home.md"), markdownPageBuilder.toString(), "UTF-8");
    }

    private static void generateImage(File magFile, File pngFile) throws Exception {
        Design design = MagicFileIo.read(magFile, Main.TECHNOLOGY_REPOSITORY);
        int displayWidth = design.getWidth() * ZOOM;
        int displayHeight = design.getHeight() * ZOOM;
        BufferedImage image = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            new MyDesignPainter(design).paintDesign(g, design, ZOOM);
        } finally {
            g.dispose();
        }
        ImageIO.write(image, "png", pngFile);
    }

    private static final class MyDesignPainter extends DesignPainter {

        private final Design design;

        public MyDesignPainter(Design design) {
            this.design = design;
        }

        private Material getPixel(PlaneSchema planeSchema, int x, int y) {
            Plane plane = design.getPlane(planeSchema);
            return plane.getPixel(x, y);
        }

        @Override
        protected void drawPixel(Graphics2D g, int pixelX, int pixelY, int screenX, int screenY, int screenSize) {

            // read pixel per plane
            Material wellPlane = getPixel(ConceptSchemas.PLANE_WELL, pixelX, pixelY);
            Material diffPlane = getPixel(ConceptSchemas.PLANE_DIFF, pixelX, pixelY);
            Material polyPlane = getPixel(ConceptSchemas.PLANE_POLY, pixelX, pixelY);
            Material metal1Plane = getPixel(ConceptSchemas.PLANE_METAL1, pixelX, pixelY);
            Material metal2Plane = getPixel(ConceptSchemas.PLANE_METAL2, pixelX, pixelY);
            Material padPlane = getPixel(ConceptSchemas.PLANE_PAD, pixelX, pixelY);

            if (wellPlane != Material.NONE) {
                g.setPaint(wellPlane == ConceptSchemas.MATERIAL_NWELL ? getHatching(0x0000ff, 0) : getHatching(0xff0000, 0));
                g.fillRect(screenX, screenY, screenSize, screenSize);
            }
            if (diffPlane != Material.NONE) {
                g.setPaint(diffPlane == ConceptSchemas.MATERIAL_NDIFF ? new Color(0x0000ff) : new Color(0xff0000));
                g.fillRect(screenX, screenY, screenSize, screenSize);
            }
            if (polyPlane != Material.NONE) {
                g.setPaint(new Color(0, 128, 0));
                g.fillRect(screenX, screenY, screenSize, screenSize);
            }
            if (metal1Plane != Material.NONE) {
                if (metal1Plane == ConceptSchemas.MATERIAL_CONTACT) {
                    g.setPaint(getHatching(0x404040, 4, true));
                } else {
                    g.setPaint(getHatching(0xc0c0c0, 4, false));
                }
                g.fillRect(screenX, screenY, screenSize, screenSize);
            }
            if (metal2Plane != Material.NONE) {
                if (metal2Plane == ConceptSchemas.MATERIAL_VIA12) {
                    g.setPaint(getHatching(0x008080, 0, false));
                } else {
                    g.setPaint(getHatching(0x00c0c0, 0, false));
                }
                g.fillRect(screenX, screenY, screenSize, screenSize);
            }
            if (padPlane != Material.NONE) {
                g.setPaint(getHatching(0xff00ff, 0, false));
                g.fillRect(screenX, screenY, screenSize, screenSize);
            }

        }

    };


}
