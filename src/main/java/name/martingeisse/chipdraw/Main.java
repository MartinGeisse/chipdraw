package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.technology.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.technology.Technology;
import name.martingeisse.chipdraw.technology.TechnologyId;
import name.martingeisse.chipdraw.technology.TechnologyRepository;

public class Main {

    public static void main(String[] args) {

        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();
        technologyRepository.add(new Technology(new TechnologyId(0, 0, 0, 0, "default"), 3));

        Workbench workbench = new Workbench(technologyRepository);

        new MainWindow(workbench).setVisible(true);

    }

}
