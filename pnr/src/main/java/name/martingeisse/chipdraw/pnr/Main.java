package name.martingeisse.chipdraw.pnr;

import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.cell.simple.SimpleCellLibraryRepository;
import name.martingeisse.chipdraw.pnr.design.Technologies;
import name.martingeisse.chipdraw.pnr.ui.MainWindow;

public class Main {

    public static void main(String[] args) {
        SimpleCellLibraryRepository technologyRepository = new SimpleCellLibraryRepository();
        technologyRepository.add(Technologies.Concept.TECHNOLOGY);
        technologyRepository.add(Technologies.LibreSiliconMagicScmos.TECHNOLOGY);
        Workbench workbench = new Workbench(technologyRepository);
        Design design = new Design(Technologies.Concept.TECHNOLOGY, 200, 200);
        new MainWindow(workbench, design).setVisible(true);
    }

}
