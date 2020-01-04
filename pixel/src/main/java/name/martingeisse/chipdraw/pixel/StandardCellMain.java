package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.SimpleTechnologyRepository;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellTemplateGeneratorBase;
import name.martingeisse.chipdraw.pixel.libre_silicon.LibreSiliconTechnologies;
import name.martingeisse.chipdraw.pixel.ui.LoadAndSaveDialogs;
import name.martingeisse.chipdraw.pixel.ui.MainWindow;

import java.io.File;

public class StandardCellMain {

    public static void main(String[] args) {
        LoadAndSaveDialogs.SUGGESTED_FOLDER = new File("resource/cell-lib/v2");
        Design design = new StandardCellTemplateGeneratorBase().generate(LibreSiliconTechnologies.TEST000_CONCEPT_MG_70_7_TECHNOLOGY);
        new MainWindow(Main.WORKBENCH, design).setVisible(true);
    }

}
