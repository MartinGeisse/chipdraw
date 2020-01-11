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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Generator {

    private static final int INITIAL_X_MARGIN = 5;
    private static final int FINAL_X_MARGIN = 5;
    private static final int INTER_ROW_X_MARGIN = 3;

    private final StandardCellTemplateGeneratorBase templateGenerator = new StandardCellTemplateGeneratorBase();
    private int[][] pmosTransistors = new int[0][0];
    private int[][] nmosTransistors = new int[0][0];
    private boolean hasPmosBackSideTrace = false;
    private boolean hasNmosBackSideTrace = false;
    private Consumer<PostProcessingContext> postProcessor = null;

    private int currentX = 0, currentY = 0;
    private int width = 0;
    private RectangleConsumer rectangleConsumer;

    private List<Integer> pmosGateX = new ArrayList<>();
    private int pmosGateY = 0;
    private List<Integer> nmosGateX = new ArrayList<>();
    private int nmosGateY = 0;

    private List<Integer> pmosContactMetalX = new ArrayList<>();
    private int pmosContactMetalY = 0;
    private List<Integer> nmosContactMetalX = new ArrayList<>();
    private int nmosContactMetalY = 0;

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

    public Generator post(Consumer<PostProcessingContext> postProcessor) {
        this.postProcessor = postProcessor;
        return this;
    }

    public void generate(File file) throws IOException {

        // determine width
        rectangleConsumer = (x, y, w, h, material) -> {
            width = Math.max(width, x + w);
        };
        generateRectangles();
        width += FINAL_X_MARGIN;
        if (width % 7 != 0) {
            width += 7 - (width % 7);
        }

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
        pmosGateY = currentY;
        pmosContactMetalY = currentY + 1;
        generateTransistorRectangles(pmosTransistors, ConceptSchemas.MATERIAL_PDIFF, 2, pmosGateX, pmosContactMetalX);

        // NMOS
        currentY = templateGenerator.getHeight() - templateGenerator.getPowerRailBottomMargin()
                - templateGenerator.getPowerRailHeight() - (hasNmosBackSideTrace ? 6 : 1) - 6;
        nmosGateY = currentY;
        nmosContactMetalY = currentY + 1;
        generateTransistorRectangles(nmosTransistors, ConceptSchemas.MATERIAL_NDIFF, 1, nmosGateX, nmosContactMetalX);

        // post-processing
        if (postProcessor != null) {
            postProcessor.accept(new MyPostProcessingContext());
        }

    }

    private void generateTransistorRectangles(int[][] transistorSpec, Material diffusionMaterial, int size,
                                              List<Integer> gateXDestination, List<Integer> contactMetalXDestination) {
        currentX = INITIAL_X_MARGIN - INTER_ROW_X_MARGIN;
        for (int[] rowSpec : transistorSpec) {
            currentX += INTER_ROW_X_MARGIN;
            int rowStartX = currentX;
            drawContact(size, contactMetalXDestination);
            for (int gateGroup : rowSpec) {
                currentX--;
                drawGates(gateGroup, size, gateXDestination);
                currentX--;
                drawContact(size, contactMetalXDestination);
            }
            rectangleConsumer.consume(rowStartX, currentY, currentX - rowStartX, 2 + size * 4, diffusionMaterial);
        }
    }

    private void drawContact(int size, List<Integer> contactMetalXDestination) {
        contactMetalXDestination.add(currentX + 1);
        rectangleConsumer.consume(currentX + 1, currentY + 1, 4, size * 4, ConceptSchemas.MATERIAL_METAL1);
        for (int i = 0; i < size; i++) {
            rectangleConsumer.consume(currentX + 2, currentY + 2 + i * 4, 2, 2, ConceptSchemas.MATERIAL_CONTACT);
        }
        currentX += 6;
    }

    private void drawGates(int number, int size, List<Integer> gateXDestination) {
        for (int i = 0; i < number; i++) {
            drawGate(size, gateXDestination);
        }
    }

    private void drawGate(int size, List<Integer> gateXDestination) {
        gateXDestination.add(currentX + 1);
        rectangleConsumer.consume(currentX + 1, currentY - 2, 2, 6 + size * 4, ConceptSchemas.MATERIAL_POLY);
        currentX += 4;
    }

    private interface RectangleConsumer {
        void consume(int x, int y, int width, int height, Material material);
    }

    private final class MyPostProcessingContext implements PostProcessingContext {

        private void connectPoly(int x1, int y1, int x2, int y2) {
            if (y1 > y2) {
                // initially towards -y (typically NMOS)
                rectangleConsumer.consume(x1, y2, 2, y1 - y2 + 2, ConceptSchemas.MATERIAL_POLY);
            } else {
                // initially towards +y (typically PMOS)
                rectangleConsumer.consume(x1, y1, 2, y2 - y1 + 2, ConceptSchemas.MATERIAL_POLY);
            }
            if (x1 > x2) {
                // turn towards -x
                rectangleConsumer.consume(x2, y2, x1 - x2 + 2, 2, ConceptSchemas.MATERIAL_POLY);
            } else {
                // turn towards +x
                rectangleConsumer.consume(x1, y2, x2 - x1 + 2, 2, ConceptSchemas.MATERIAL_POLY);
            }
        }

        @Override
        public void connectPmosGate(int globalGateIndex, int targetX, int targetY) {
            connectPoly(pmosGateX.get(globalGateIndex), pmosGateY, targetX, targetY);
        }

        @Override
        public void connectNmosGate(int globalGateIndex, int targetX, int targetY) {
            connectPoly(nmosGateX.get(globalGateIndex), nmosGateY, targetX, targetY);
        }

        @Override
        public void crossConnectGatesWithMetal(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int metalX, int metalY,
                                               int polyPadDisplacementX, int polyPadDisplacementY) {

            // draw poly pad
            int polyPadX = metalX - 1 + polyPadDisplacementX;
            int polyPadY = metalY - 1 + polyPadDisplacementY;
            rectangleConsumer.consume(polyPadX, polyPadY, 6, 6, ConceptSchemas.MATERIAL_POLY);

            // connect gates to poly pad
            connectPmosGate(pmosGlobalGateIndex, polyPadX + 2, polyPadY);
            connectNmosGate(nmosGlobalGateIndex, polyPadX + 2, polyPadY + 4);

            // draw metal and contact
            int metalStartX = Math.min(metalX, polyPadX + 1);
            int metalStartY = Math.min(metalY, polyPadY + 1);
            int metalEndX = Math.max(metalX + 4, polyPadX + 5);
            int metalEndY = Math.max(metalY + 4, polyPadY + 5);
            rectangleConsumer.consume(metalStartX, metalStartY, metalEndX - metalStartX, metalEndY - metalStartY, ConceptSchemas.MATERIAL_METAL1);
            rectangleConsumer.consume(polyPadX + 2, polyPadY + 2, 2, 2, ConceptSchemas.MATERIAL_CONTACT);

        }

        @Override
        public void crossConnectGatesWithPort(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int portTileX, int portTileY,
                                              int polyPadDisplacementX, int polyPadDisplacementY) {
            crossConnectGatesWithMetal(pmosGlobalGateIndex, nmosGlobalGateIndex,
                    portTileX * 7 + 1, portTileY * 7 + 1, polyPadDisplacementX, polyPadDisplacementY);

        }

        @Override
        public void connectPmosContactToPower(int... pmosGlobalContactIndexes) {
            int height = (hasPmosBackSideTrace ? 7 : 2);
            for (int pmosGlobalContactIndex : pmosGlobalContactIndexes) {
                rectangleConsumer.consume(pmosContactMetalX.get(pmosGlobalContactIndex), pmosContactMetalY - height,
                        4, height, ConceptSchemas.MATERIAL_METAL1);
            }
        }

        @Override
        public void connectNmosContactToPower(int... nmosGlobalContactIndexes) {
            int height = (hasNmosBackSideTrace ? 7 : 2);
            for (int nmosGlobalContactIndex : nmosGlobalContactIndexes) {
                rectangleConsumer.consume(nmosContactMetalX.get(nmosGlobalContactIndex), nmosContactMetalY + 4,
                        4, height, ConceptSchemas.MATERIAL_METAL1);
            }
        }

    }
}
