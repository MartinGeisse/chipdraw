package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.pixel.design.Technologies;
import name.martingeisse.chipdraw.pixel.ui.MainWindow;

public class Main {

    public static void main(String[] args) {
        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();
        technologyRepository.add(Technologies.Concept.TECHNOLOGY);
        technologyRepository.add(Technologies.LibreSiliconMagicScmos.TECHNOLOGY);
        Workbench workbench = new Workbench(technologyRepository);
        Design design = new Design(Technologies.Concept.TECHNOLOGY, 200, 200);
        new MainWindow(workbench, design).setVisible(true);
    }

}
