package name.martingeisse.chipdraw.pnr.ui;

import name.martingeisse.chipdraw.pnr.cell.CellLibraryRepository;
import name.martingeisse.chipdraw.pnr.cell.NoSuchCellLibraryException;
import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.design.DesignPersistence;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;

public final class LoadAndSaveDialogs {

    public static final String MEMORY_DUMP_FILENAME_EXTENSION = "ChipdrawPnrMemoryDump";

    private static final FileNameExtensionFilter FILE_NAME_EXTENSION_FILTER = new FileNameExtensionFilter("Chipdraw PNR memory dump", MEMORY_DUMP_FILENAME_EXTENSION);

    private final DesignPersistence designPersistence;

    public LoadAndSaveDialogs(CellLibraryRepository cellLibraryRepository) {
        this.designPersistence = new DesignPersistence(cellLibraryRepository);
    }

    public void showSaveDialog(Component parent, Design design) {
        String path = chooseFile(parent, JFileChooser.SAVE_DIALOG);
        if (path == null) {
            return;
        }
        try {
            designPersistence.save(design, path);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Error while saving: " + e);
        }
    }

    public Design showLoadDialog(Component parent) throws NoSuchCellLibraryException {
        String path = chooseFile(parent, JFileChooser.OPEN_DIALOG);
        if (path == null) {
            return null;
        }
        try {
            return designPersistence.load(path);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Error while loading: " + e);
            return null;
        }
    }

    private static String chooseFile(Component parent, int dialogType) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(dialogType);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(FILE_NAME_EXTENSION_FILTER);
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            if (!path.endsWith('.' + MEMORY_DUMP_FILENAME_EXTENSION)) {
                path = path + '.' + MEMORY_DUMP_FILENAME_EXTENSION;
            }
            return path;
        } else {
            return null;
        }
    }

}