package name.martingeisse.chipdraw.pnr;

import name.martingeisse.chipdraw.pnr.cell.CellTemplate;
import name.martingeisse.chipdraw.pnr.cell.simple.SimpleCellLibrary;
import name.martingeisse.chipdraw.pnr.cell.simple.SimpleCellLibraryRepository;
import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.ui.MainWindow;

public class Main {

    public static void main(String[] args) {

        SimpleCellLibrary cellLibrary = new SimpleCellLibrary("test");
        cellLibrary.add(new CellTemplate("not"));
        cellLibrary.add(new CellTemplate("nand"));
        cellLibrary.add(new CellTemplate("nor"));

        SimpleCellLibraryRepository cellLibraryRepository = new SimpleCellLibraryRepository();
        cellLibraryRepository.add(cellLibrary);

        Workbench workbench = new Workbench(cellLibraryRepository);
        Design design = new Design(cellLibrary, 50, 50);
        new MainWindow(workbench, design).setVisible(true);
    }

}
