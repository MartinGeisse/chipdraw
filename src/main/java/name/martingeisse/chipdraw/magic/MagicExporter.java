package name.martingeisse.chipdraw.magic;

import name.martingeisse.chipdraw.Design;
import name.martingeisse.chipdraw.Plane;
import name.martingeisse.chipdraw.extractor.CornerStitchingExtrator;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class MagicExporter {

    private final Design design;
    private final File file;

    public MagicExporter(Design design, File file) {
        this.design = design;
        this.file = file;
    }

    public void export() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.ISO_8859_1)) {
                PrintWriter out = new PrintWriter(outputStreamWriter);
                export(out);
                out.flush();
            }
        }
    }

    private void export(PrintWriter out) {
        out.println("magic");
        out.println("tech scmos");
        out.println("timestamp " + new Date().getTime());

        // wells
        printSectionHeadline(out, "pwell");
        printRectangles(out, design.getPlanes().get(0), 1);
        printSectionHeadline(out, "nwell");
        printRectangles(out, design.getPlanes().get(0), 0);

        // TODO

        printSectionHeadline(out, "end");
    }

    private void printSectionHeadline(PrintWriter out, String layerName) {
        out.println("<< " + layerName + " >>");
    }

    private void printRectangles(PrintWriter out, Plane plane, int localMaterialIndexFilter) {
        new CornerStitchingExtrator() {
            @Override
            protected void finishRectangle(int localMaterialIndex, int x, int y, int width, int height) {
                if (localMaterialIndex == localMaterialIndexFilter) {
                    // TODO turns the design upside-down since in Magic the y axis points upwards
                    out.println("rect " + x + " " + y + " " + (x + width) + " " + (y + height));
                }
            }
        }.extract(design);
    }

}
