package name.martingeisse.chipdraw.pixel.global_tools.stdcell;

import name.martingeisse.chipdraw.pixel.design.Design;

public class StandardCellExtender {

    private final int amount;
    private final boolean left;

    public StandardCellExtender(int amount, boolean left) {
        this.amount = amount;
        this.left = left;
    }

    public Design extend(Design oldDesign) {

        Design newDesign = new Design(oldDesign.getTechnology(), oldDesign.getWidth() + amount, oldDesign.getHeight());
        newDesign.copyFrom(oldDesign, 0, 0, left ? amount : 0, 0, oldDesign.getWidth(), oldDesign.getHeight());

        StandardCellTemplateGeneratorBase generator = new StandardCellTemplateGeneratorBase();
        generator.setWidth(newDesign.getWidth());
        Design template = generator.generate();
        newDesign.copyFrom(template, left ? 0 : oldDesign.getWidth(), 0, left ? 0 : oldDesign.getWidth(), 0, amount, oldDesign.getHeight());
        {
            int h = generator.getWellTapMargin() + generator.getWellTapSize() + generator.getOverlapByDiffusion();
            newDesign.copyFrom(template, 0, 0, 0, 0, template.getWidth(), h);
            int y = oldDesign.getHeight() - h;
            newDesign.copyFrom(template, 0, y, 0, y, template.getWidth(), h);
        }

        return newDesign;
    }
}
