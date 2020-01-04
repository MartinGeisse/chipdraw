package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.pixel.libre_silicon.LibreSiliconTechnologies;
import name.martingeisse.chipdraw.pixel.ui.MainWindow;

public class Main {

    public static final SimpleTechnologyRepository TECHNOLOGY_REPOSITORY = new SimpleTechnologyRepository();
    static {
        TECHNOLOGY_REPOSITORY.add(LibreSiliconTechnologies.TEST000_CONCEPT_TECHNOLOGY);
        TECHNOLOGY_REPOSITORY.add(LibreSiliconTechnologies.TEST000_CONCEPT_MG_70_7_TECHNOLOGY);
        TECHNOLOGY_REPOSITORY.add(LibreSiliconTechnologies.MagicScmos.TECHNOLOGY);
    }

    public static final Workbench WORKBENCH = new Workbench(TECHNOLOGY_REPOSITORY);

    public static void main(String[] args) {
        Design design = new Design(LibreSiliconTechnologies.TEST000_CONCEPT_TECHNOLOGY, 200, 200);
        new MainWindow(WORKBENCH, design).setVisible(true);
    }

}
