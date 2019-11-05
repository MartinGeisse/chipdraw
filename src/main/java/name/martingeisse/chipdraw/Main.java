package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.technology.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.technology.Technologies;

public class Main {

    public static void main(String[] args) {
        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();
        technologyRepository.add(Technologies.CONCEPT);
        Workbench workbench = new Workbench(technologyRepository);
        Design design = new Design(Technologies.CONCEPT, 20, 10);
        new MainWindow(workbench, design).setVisible(true);
    }

}
