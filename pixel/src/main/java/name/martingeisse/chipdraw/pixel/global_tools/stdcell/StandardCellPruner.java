package name.martingeisse.chipdraw.pixel.global_tools.stdcell;

import name.martingeisse.chipdraw.pixel.design.Design;

public class StandardCellPruner {

    public Design prune(Design originalDesign) {
        return originalDesign.createCopyOfRectangle(0, 0, originalDesign.getWidth() - 5, originalDesign.getHeight());
    }

}
