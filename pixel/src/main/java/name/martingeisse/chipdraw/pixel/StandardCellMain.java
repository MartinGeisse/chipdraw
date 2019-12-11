package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.pixel.design.Technologies;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellTemplateGeneratorBase;
import name.martingeisse.chipdraw.pixel.ui.MainWindow;

public class StandardCellMain {

    public static void main(String[] args) {
        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();
        technologyRepository.add(Technologies.Concept.TECHNOLOGY);
        technologyRepository.add(Technologies.LibreSiliconMagicScmos.TECHNOLOGY);
        Workbench workbench = new Workbench(technologyRepository);
        Design design = new StandardCellTemplateGeneratorBase().generate();
        new MainWindow(workbench, design).setVisible(true);
    }

}
