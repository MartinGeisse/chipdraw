package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.pixel.scmos.ScmosConcept;
import name.martingeisse.chipdraw.pixel.scmos.magic.ScmosMagic;
import name.martingeisse.chipdraw.pixel.ui.MainWindow;

public class Main {

    public static final SimpleTechnologyRepository TECHNOLOGY_REPOSITORY = new SimpleTechnologyRepository();
    static {
        TECHNOLOGY_REPOSITORY.add(ScmosConcept.TECHNOLOGY);
        TECHNOLOGY_REPOSITORY.add(ScmosMagic.TECHNOLOGY);
    }

    public static final Workbench WORKBENCH = new Workbench(TECHNOLOGY_REPOSITORY);

    public static void main(String[] args) {
        Design design = new Design(ScmosConcept.TECHNOLOGY, 200, 200);
        new MainWindow(WORKBENCH, design).setVisible(true);
    }

}
