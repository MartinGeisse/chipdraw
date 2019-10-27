package name.martingeisse.chipdraw;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

public final class LoadAndSaveDialogs {

    public static final String MEMORY_DUMP_FILENAME_EXTENSION = "ChipdrawMemoryDump";

    private static final FileNameExtensionFilter FILE_NAME_EXTENSION_FILTER = new FileNameExtensionFilter("Chipdraw memory dump", MEMORY_DUMP_FILENAME_EXTENSION);

    private LoadAndSaveDialogs() {
    }

    public static void showSaveDialog(Component parent, Design design) {
        String path = chooseFile(parent, JFileChooser.SAVE_DIALOG);
        if (path != null) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(design);
                objectOutputStream.flush();
                System.out.println("saved to: " + path);
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(parent, "Error while saving: " + exception);
            }
        }
    }

    public static Design showLoadDialog(Component parent) {
        String path = chooseFile(parent, JFileChooser.OPEN_DIALOG);
        if (path == null) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            System.out.println("loading from: " + path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Design) objectInputStream.readObject();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(parent, "Error while loading: " + exception);
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
