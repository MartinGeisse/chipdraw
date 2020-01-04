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

    private final File file;
    private final ImmutableList<Integer> mintermArities;
    private final StandardCellTemplateGeneratorBase templateGenerator = new StandardCellTemplateGeneratorBase();
    private int currentX = 0, currentY = 0;
    private int width = 0;
    private RectangleConsumer rectangleConsumer;

    public Generator(File file, Integer... mintermArities) {
        this(file, ImmutableList.copyOf(mintermArities));
    }

    public Generator(File file, ImmutableList<Integer> mintermArities) {
        this.file = file;
        this.mintermArities = mintermArities;
    }

    public void generate() throws IOException {

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

        // write file
        MagicFileIo.write(design, file, design.getTechnology().getId(), true);

    }

    private void generateRectangles() {

        // PMOS
        // TODO we only need a back-side metal trace if any minterm except the first one has an arity >1. The first one
        // is special because it connects to the power rail which acts as an implicit back-side trace.
        // A reduced-height cell library might make use of that by offering only complex gates for which at most one
        // minterm has an arity >1, so a back-side trace is never needed.
        currentY = templateGenerator.getPowerRailTopMargin() + templateGenerator.getPowerRailHeight() + 6; // s/+6/+1 without back-side trace
        generateMintermPmosRectangles();

        // NMOS
        currentY = templateGenerator.getHeight() - templateGenerator.getPowerRailBottomMargin() - templateGenerator.getPowerRailHeight() - 7;
        generateMintermNmosRectangles();

    }

    private void generateMintermNmosRectangles() {
        currentX = 5;
        drawContact(0, 2);
        boolean isOutput = true;
        for (int mintermArity : mintermArities) {
            currentX--;
            drawGates(mintermArity);
            currentX--;
            drawContact(isOutput ? 5 : 0, isOutput ? 0 : 2);
            isOutput = !isOutput;
        }
        rectangleConsumer.consume(5, currentY, currentX - 5, 6, ConceptSchemas.MATERIAL_NDIFF);
    }

    private void generateMintermPmosRectangles() {
        currentX = 5;
        boolean lastContactOfPreviousMintermIsOutput = false;
        for (int mintermIndex = 0; mintermIndex < mintermArities.size(); mintermIndex++) {
            int mintermArity = mintermArities.get(mintermIndex);

            // If the previous minterm ends with an output contact, then we share that contact.
            // Otherwise, we start the minterm in such a way that the last contact is an output.
            boolean currentContactIsOutput;
            if (lastContactOfPreviousMintermIsOutput) {
                currentContactIsOutput = false;
            } else {
                currentContactIsOutput = (mintermArity % 2 == 0);
                currentX += 2;
            }

            // draw gates and subsequent contacts
            for (int i = 0; i < mintermArity; i++) {

                // draw contact
                // TODO share contact with previous minterm if possible
                // TODO remove the contact altogether if possible
                // TODO for the last minterm (or other cases?) another rule makes sense: In doubt, reduce number of output contacts (= capacity!)
//                if (i > 0 || !lastContactOfPreviousMintermIsOutput) {
                drawPmosContact(mintermIndex, currentContactIsOutput);
//                }

                // draw gate
                currentX--;
                drawGate();
                currentX--;

                currentContactIsOutput = !currentContactIsOutput;
            }

            // draw last contact
            drawPmosContact(mintermIndex, currentContactIsOutput);
            lastContactOfPreviousMintermIsOutput = currentContactIsOutput;

        }
    }

    private void drawPmosContact(int mintermIndex, boolean isOutput) {
        boolean down = ((mintermIndex % 2 == 0) == isOutput);
        drawContact(down ? 0 : mintermIndex == 0 ? 7 : 5, down ? 5 : 0);
    }

    private void skip(int amount) {
        currentX += amount;
    }

    private void drawContact(int extendUpwards, int extendDownwards) {
        rectangleConsumer.consume(currentX + 1, currentY + 1 - extendUpwards, 4, 4 + extendUpwards + extendDownwards, ConceptSchemas.MATERIAL_METAL1);
        rectangleConsumer.consume(currentX + 2, currentY + 2, 2, 2, ConceptSchemas.MATERIAL_CONTACT);
        currentX += 6;
    }

    private void drawGates(int number) {
        for (int i = 0; i < number; i++) {
            drawGate();
        }
    }

    private void drawGate() {
        rectangleConsumer.consume(currentX + 1, currentY - 2, 2, 10, ConceptSchemas.MATERIAL_POLY);
        currentX += 4;
    }

    private interface RectangleConsumer {
        void consume(int x, int y, int width, int height, Material material);
    }

}
