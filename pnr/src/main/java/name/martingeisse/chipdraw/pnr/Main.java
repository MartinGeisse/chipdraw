package name.martingeisse.chipdraw.pnr;

import name.martingeisse.chipdraw.pnr.cell.CellTemplate;
import name.martingeisse.chipdraw.pnr.cell.simple.SimpleCellLibrary;
import name.martingeisse.chipdraw.pnr.cell.simple.SimpleCellLibraryRepository;
import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.ui.MainWindow;

public class Main {

    public static void main(String[] args) {

        SimpleCellLibrary cellLibrary = new SimpleCellLibrary("test");
        cellLibrary.add(new CellTemplate("not", 6, 10,  context -> {
            context.drawLine(30, 30, 60, 50);
            context.drawLine(30, 70, 60, 50);
            context.drawLine(30, 30, 30, 70);
            context.drawCircle(64, 50, 4);
        }));
        cellLibrary.add(new CellTemplate("nand", 10, 10, context -> {
            context.drawLine(30, 30, 30, 70);
            context.drawLine(30, 30, 60, 30);
            context.drawLine(60, 30, 70, 40);
            context.drawLine(30, 70, 60, 70);
            context.drawLine(60, 70, 70, 60);
            context.drawLine(70, 40, 70, 60);
            context.drawCircle(75, 50, 5);
        }));
        cellLibrary.add(new CellTemplate("nor", 10, 10, context -> {
            context.drawLine(30, 30, 40, 40);
            context.drawLine(30, 70, 40, 60);
            context.drawLine(40, 40, 40, 60);
            context.drawLine(30, 30, 60, 30);
            context.drawLine(60, 30, 70, 40);
            context.drawLine(30, 70, 60, 70);
            context.drawLine(60, 70, 70, 60);
            context.drawLine(70, 40, 70, 60);
            context.drawCircle(75, 50, 5);
        }));

        SimpleCellLibraryRepository cellLibraryRepository = new SimpleCellLibraryRepository();
        cellLibraryRepository.add(cellLibrary);

        Workbench workbench = new Workbench(cellLibraryRepository);
        Design design = new Design(cellLibrary, 50, 50);
        new MainWindow(workbench, design).setVisible(true);
    }

}
