package name.martingeisse.chipdraw.pixel.generate;

import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.global_tools.magic.MagicFileIo;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellTemplateGeneratorBase;
import name.martingeisse.chipdraw.pixel.libre_silicon.LibreSiliconTechnologies;
import name.martingeisse.chipdraw.pixel.operation.SimpleOperationExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CellBuilder {

    private final File file;
    private final List<Element> nmosElements = new ArrayList<>();
    private final List<Element> pmosElements = new ArrayList<>();
    private int vddRailHeightandMargin;
    private int gndRailHeightandMargin;

    public CellBuilder(File file) {
        this.file = file;
    }

    public List<Element> getNmosElements() {
        return nmosElements;
    }

    public void addNmosElement(Element element) {
        nmosElements.add(element);
    }

    public List<Element> getPmosElements() {
        return pmosElements;
    }

    public void addPmosElement(Element element) {
        pmosElements.add(element);
    }

    public void build() throws Exception {

        int totalNmosWidth = getTotalWidth(nmosElements);
        int totalPmosWidth = getTotalWidth(pmosElements);
        int totalWidth = Math.max(totalNmosWidth, totalPmosWidth) + 10;
        totalWidth = (totalWidth % 7) == 0 ? totalWidth : (totalWidth + 7 - totalWidth % 7);

        StandardCellTemplateGeneratorBase templateGenerator = new StandardCellTemplateGeneratorBase();
        templateGenerator.setWidth(totalWidth);
        SimpleOperationExecutor executor = new SimpleOperationExecutor(templateGenerator.generate(LibreSiliconTechnologies.TEST000_CONCEPT_MG_70_7_TECHNOLOGY));

        vddRailHeightandMargin = templateGenerator.getPowerRailTopMargin() + templateGenerator.getPowerRailHeight();
        gndRailHeightandMargin = templateGenerator.getPowerRailBottomMargin() + templateGenerator.getPowerRailHeight();

        draw(executor, 5, nmosElements, ConceptSchemas.MATERIAL_NDIFF);
        draw(executor, 5, pmosElements, ConceptSchemas.MATERIAL_PDIFF);

        Design design = executor.getDesign();
        try {
            MagicFileIo.write(design, file, design.getTechnology().getId(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getTotalWidth(List<Element> elements) {
        int result = 0;
        for (Element element : elements) {
            result += element.getWidth();
        }
        return result;
    }

    private void draw(SimpleOperationExecutor executor, int x, List<Element> elements, Material diffusionMaterial) throws Exception {
        for (Element element : elements) {
            int y;
            if (diffusionMaterial == ConceptSchemas.MATERIAL_PDIFF) {
                y = vddRailHeightandMargin + element.getRailSpacing();
            } else {
                y = executor.getDesign().getHeight() - gndRailHeightandMargin - element.getRailSpacing() - element.getHeight();
            }

            element.draw(executor, x, y, diffusionMaterial);
            x  += element.getWidth();
        }
    }

}
