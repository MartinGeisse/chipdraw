package name.martingeisse.chipdraw.pixel.ui;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.NoSuchTechnologyException;
import name.martingeisse.chipdraw.pixel.design.TechnologyRepository;
import name.martingeisse.chipdraw.pixel.global_tools.magic.MagicFileIo;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public final class LoadAndSaveDialogs {

    public static final String FILE_NAME_EXTENSION = "mag";

    private static final FileNameExtensionFilter FILE_NAME_EXTENSION_FILTER = new FileNameExtensionFilter("Design file", FILE_NAME_EXTENSION);

    private final TechnologyRepository technologyRepository;

    public LoadAndSaveDialogs(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    public void showSaveDialog(Component parent, Design design) {
        String path = chooseFile(parent, JFileChooser.SAVE_DIALOG);
        if (path == null) {
            return;
        }
        try {
            MagicFileIo.write(design, new File(path), design.getTechnology().getId());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Error while saving: " + e);
        }
    }

    public Design showLoadDialog(Component parent) throws NoSuchTechnologyException, UserVisibleMessageException {
        String path = chooseFile(parent, JFileChooser.OPEN_DIALOG);
        if (path == null) {
            return null;
        }
        try {
            return MagicFileIo.read(new File(path), technologyRepository);
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
            if (!path.endsWith('.' + FILE_NAME_EXTENSION)) {
                path = path + '.' + FILE_NAME_EXTENSION;
            }
            return path;
        } else {
            return null;
        }
    }

}
