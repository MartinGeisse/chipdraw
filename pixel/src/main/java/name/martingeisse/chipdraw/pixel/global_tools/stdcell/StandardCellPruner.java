package name.martingeisse.chipdraw.pixel.global_tools.stdcell;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

public class StandardCellPruner {

    public Design prune(Design originalDesign) throws UserVisibleMessageException {

        // generate template
        StandardCellTemplateGeneratorBase generator = new StandardCellTemplateGeneratorBase();
        generator.setWidth(originalDesign.getWidth());
        generator.setHeight(originalDesign.getHeight());
        Design template = generator.generate(originalDesign.getTechnology());

        // find rightmost difference
        int modifiedWidth = originalDesign.getWidth();
        while (Design.rectangleEquals(originalDesign, template, modifiedWidth - 1, 0, 1, originalDesign.getHeight())) {
            modifiedWidth--;
            if (modifiedWidth == 0) {
                throw new UserVisibleMessageException("cannot prune: design is equal to template");
            }
        }

        // leave some margin to finish the cropped design manually
        modifiedWidth += 10;
        if (modifiedWidth >= originalDesign.getWidth()) {
            return new Design(originalDesign);
        }

        // crop
        return originalDesign.createCopyOfRectangle(0, 0, modifiedWidth, originalDesign.getHeight());

    }

}
