package name.martingeisse.chipdraw.pixel.generate.b;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellTemplateGeneratorBase;
import name.martingeisse.chipdraw.pixel.libre_silicon.LibreSiliconTechnologies;

import java.io.File;

public class Generator {

    private final File file;
    private final ImmutableList<Integer> mintermArities;
    private final StandardCellTemplateGeneratorBase templateGenerator = new StandardCellTemplateGeneratorBase();
    private int currentX = 0, currentY = 0;
    private int width = 0;
    private RectangleConsumer rectangleConsumer;
    private boolean lastContactIsMintermOutput;

    public Generator(File file, Integer... mintermArities) {
        this(file, ImmutableList.copyOf(mintermArities));
    }

    public Generator(File file, ImmutableList<Integer> mintermArities) {
        this.file = file;
        this.mintermArities = mintermArities;
    }

    public void generate() {

        // determine width
        rectangleConsumer = (x, y, w, h, material) -> {
            width = Math.max(width, x + w);
        };
        generateRectangles();
        width += 5;

        // generate cell
        templateGenerator.setWidth(width);
        Design design = templateGenerator.generate(LibreSiliconTechnologies.TEST000_CONCEPT_MG_70_7_TECHNOLOGY);
        rectangleConsumer = (x, y, w, h, material) -> {
            design.getPlane(material.getPlaneSchema()).drawRectangleAutoclip(x, y, w, h, material);
        };
        generateRectangles();

    }

    private void generateRectangles() {
        currentY = templateGenerator.getPowerRailTopMargin() + templateGenerator.getPowerRailHeight() + 1;
        lastContactIsMintermOutput = false;
        generateMintermNmosRectangles();
        currentY = templateGenerator.getHeight() - templateGenerator.getPowerRailBottomMargin() - templateGenerator.getPowerRailHeight() - 1;
        lastContactIsMintermOutput = false;
        generateMintermPmosRectangles();
    }

    private void generateMintermNmosRectangles() {
        currentX = 5;
        drawContact();
        for (int mintermArity : mintermArities) {
            currentX--;
            drawGates(mintermArity);
            currentX--;
            drawContact();
        }
        rectangleConsumer.consume(5, currentY, currentX - 5, 6, ConceptSchemas.MATERIAL_NDIFF);
    }

    private void generateMintermPmosRectangles() {
    }

    private void skip(int amount) {
        currentX += amount;
    }

    private void drawContact() {
        rectangleConsumer.consume(currentX + 1, currentY + 1, 4, 4, ConceptSchemas.MATERIAL_METAL1);
        rectangleConsumer.consume(currentX + 2, currentY + 2, 2, 2, ConceptSchemas.MATERIAL_CONTACT);
        currentX += 6;
    }

    private void drawGates(int number) {
        for (int i = 0; i < number; i++) {
            drawGate();
        }
    }

    private void drawGate() {
        rectangleConsumer.consume(currentX + 1, currentY - 2, 2, 8, ConceptSchemas.MATERIAL_POLY);
        currentX += 4;
    }

    private interface RectangleConsumer {
        void consume(int x, int y, int width, int height, Material material);
    }

}
