package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.pixel.design.Technologies;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellTemplateGeneratorBase;
import name.martingeisse.chipdraw.pixel.ui.LoadAndSaveDialogs;
import name.martingeisse.chipdraw.pixel.ui.MainWindow;

import java.io.File;

public class StandardCellMain {

    public static void main(String[] args) {
        LoadAndSaveDialogs.SUGGESTED_FOLDER = new File("resource/cell-lib/v2");

        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();
        technologyRepository.add(Technologies.Concept.TECHNOLOGY);
        technologyRepository.add(Technologies.LibreSiliconMagicScmos.TECHNOLOGY);
        Workbench workbench = new Workbench(technologyRepository);
        Design design = new StandardCellTemplateGeneratorBase().generate();
        new MainWindow(workbench, design).setVisible(true);
    }

}
