package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.pixel.libresilicon.LibresiliconTechnologies;
import name.martingeisse.chipdraw.pixel.ui.MainWindow;

public class Main {

    public static void main(String[] args) {
        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();
        technologyRepository.add(LibresiliconTechnologies.CONCEPT_TECHNOLOGY);
        technologyRepository.add(LibresiliconTechnologies.MagicScmos.TECHNOLOGY);
        Workbench workbench = new Workbench(technologyRepository);
        Design design = new Design(LibresiliconTechnologies.CONCEPT_TECHNOLOGY, 200, 200);
        new MainWindow(workbench, design).setVisible(true);
    }

}
