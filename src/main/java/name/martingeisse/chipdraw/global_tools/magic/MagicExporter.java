package name.martingeisse.chipdraw.global_tools.magic;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.global_tools.CornerStitchingExtrator;
import name.martingeisse.chipdraw.technology.Technologies;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * The magic file format lists materials in a specific order (probably the same as in the tech file), but it is
 * possible to write them in any order. If the same pixel in the same plane is covered by multiple materials, the
 * one appearing later in the file "wins" (technically, I assume that they are applied in order so the later ones
 * "overwrite" the earlier ones). Saving that file in Magic generates the "correct" rectangles and the standard
 * material order.
 * <p>
 * We should have the DRC check that contacts are okay since they generate pixels in the connected layers automatically.
 * If that is given, we don't have any overlapping rectangles and so can export in any order.
 */
public class MagicExporter {

    private final Design originalDesign;
    private final File file;
    private Design design;
    private PrintWriter out;

    public MagicExporter(Design design, File file) {
        this.originalDesign = design;
        this.file = file;
    }

    public void export() throws IOException, ConceptToLibresiliconConverter.IncompatibilityException {
        if (originalDesign.getTechnology() == Technologies.CONCEPT) {
            design = new ConceptToLibresiliconConverter(originalDesign).convert();
        } else if (originalDesign.getTechnology() == Technologies.LIBRESILICON_MAGIC_SCMOS) {
            design = originalDesign;
        } else {
            throw new IllegalArgumentException("input design for conversion must use 'concept' or 'libresilicon-magic-scmos' technology");
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.ISO_8859_1)) {
                out = new PrintWriter(outputStreamWriter);
                printContents();
                out.flush();
                out = null;
            }
        }
        design = null;
    }

    private void printContents() {
        out.println("magic");
        out.println("tech scmos");
        out.println("timestamp " + new Date().getTime());
        for (Plane plane : design.getPlanes()) {
            for (int localMaterialIndex = 0; localMaterialIndex < plane.getSchema().getMaterialNames().size(); localMaterialIndex++) {
                if (plane.hasMaterial(localMaterialIndex)) {
                    int localMaterialIndexFinal = localMaterialIndex;
                    out.println("<< " + plane.getSchema().getMaterialNames().get(localMaterialIndex) + " >>");
                    new CornerStitchingExtrator() {
                        @Override
                        protected void finishRectangle(int rectangleLocalMaterialIndex, int x, int y, int width, int height) {
                            if (rectangleLocalMaterialIndex == localMaterialIndexFinal) {
                                int flippedY1 = design.getHeight() - y - height;
                                int flippedY2 = design.getHeight() - y;
                                out.println("rect " + x + " " + flippedY1 + " " + (x + width) + " " + flippedY2);
                            }
                        }
                    }.extract(plane);
                }
            }
        }
        out.println("<< end >>");
    }

}
