package name.martingeisse.chipdraw.pixel.generate.b;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.global_tools.magic.MagicFileIo;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellTemplateGeneratorBase;
import name.martingeisse.chipdraw.pixel.libre_silicon.LibreSiliconTechnologies;

import java.io.File;
import java.io.IOException;

public class Generator {

    private static final int INITIAL_X_MARGIN = 5;
    private static final int FINAL_X_MARGIN = 5;
    private static final int INTER_ROW_X_MARGIN = 3;

    private final StandardCellTemplateGeneratorBase templateGenerator = new StandardCellTemplateGeneratorBase();
    private int[][] pmosTransistors = new int[0][0];
    private int[][] nmosTransistors = new int[0][0];
    private boolean hasPmosBackSideTrace = false;
    private boolean hasNmosBackSideTrace = false;

    private int currentX = 0, currentY = 0;
    private int width = 0;
    private RectangleConsumer rectangleConsumer;

    public Generator pmos(int[][] pmosTransistors) {
        this.pmosTransistors = pmosTransistors;
        return this;
    }

    public Generator nmos(int[][] nmosTransistors) {
        this.nmosTransistors = nmosTransistors;
        return this;
    }

    public Generator setHasPmosBackSideTrace(boolean hasPmosBackSideTrace) {
        this.hasPmosBackSideTrace = hasPmosBackSideTrace;
        return this;
    }

    public Generator setHasNmosBackSideTrace(boolean hasNmosBackSideTrace) {
        this.hasNmosBackSideTrace = hasNmosBackSideTrace;
        return this;
    }

    public void generate(File file) throws IOException {

        // determine width
        rectangleConsumer = (x, y, w, h, material) -> {
            width = Math.max(width, x + w);
        };
        generateRectangles();
        width += FINAL_X_MARGIN;

        // generate cell
        templateGenerator.setWidth(width);
        Design design = templateGenerator.generate(LibreSiliconTechnologies.TEST000_CONCEPT_MG_70_7_TECHNOLOGY);
        rectangleConsumer = (x, y, w, h, material) -> {
            design.getPlane(material.getPlaneSchema()).drawRectangleAutoclip(x, y, w, h, material);
        };
        generateRectangles();

        // write file
        MagicFileIo.write(design, file, design.getTechnology().getId(), true);

    }

    private void generateRectangles() {

        // PMOS
        currentY = templateGenerator.getPowerRailTopMargin() + templateGenerator.getPowerRailHeight() +
                (hasPmosBackSideTrace ? 6 : 1);
        generateTransistorRectangles(pmosTransistors, ConceptSchemas.MATERIAL_PDIFF, 2);

        // NMOS
        currentY = templateGenerator.getHeight() - templateGenerator.getPowerRailBottomMargin()
                - templateGenerator.getPowerRailHeight() - (hasNmosBackSideTrace ? 6 : 1) - 6;
        generateTransistorRectangles(nmosTransistors, ConceptSchemas.MATERIAL_NDIFF, 1);

    }

    private void generateTransistorRectangles(int[][] transistorSpec, Material diffusionMaterial, int size) {
        currentX = INITIAL_X_MARGIN - INTER_ROW_X_MARGIN;
        for (int[] rowSpec : transistorSpec) {
            currentX += INTER_ROW_X_MARGIN;
            int rowStartX = currentX;
            drawContact(size);
            for (int gateGroup : rowSpec) {
                currentX--;
                drawGates(gateGroup, size);
                currentX--;
                drawContact(size);
            }
            rectangleConsumer.consume(rowStartX, currentY, currentX - rowStartX, 2 + size * 4, diffusionMaterial);
        }
    }

    private void drawContact(int size) {
        rectangleConsumer.consume(currentX + 1, currentY + 1, 4, size * 4, ConceptSchemas.MATERIAL_METAL1);
        for (int i = 0; i < size; i++) {
            rectangleConsumer.consume(currentX + 2, currentY + 2 + i * 4, 2, 2, ConceptSchemas.MATERIAL_CONTACT);
        }
        currentX += 6;
    }

    private void drawGates(int number, int size) {
        for (int i = 0; i < number; i++) {
            drawGate(size);
        }
    }

    private void drawGate(int size) {
        rectangleConsumer.consume(currentX + 1, currentY - 2, 2, 6 + size * 4, ConceptSchemas.MATERIAL_POLY);
        currentX += 4;
    }

    private interface RectangleConsumer {
        void consume(int x, int y, int width, int height, Material material);
    }

}
