package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.ui.MainWindow;

public class Main {

    public static void main(String[] args) {
        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();
        technologyRepository.add(Technologies.CONCEPT);
        technologyRepository.add(Technologies.LIBRESILICON_MAGIC_SCMOS);
        Workbench workbench = new Workbench(technologyRepository);
        Design design = new Design(Technologies.CONCEPT, 200, 200);
        new MainWindow(workbench, design).setVisible(true);
    }

}
