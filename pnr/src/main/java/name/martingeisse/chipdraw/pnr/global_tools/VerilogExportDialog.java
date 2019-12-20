package name.martingeisse.chipdraw.pnr.global_tools;

import name.martingeisse.chipdraw.pnr.design.Design;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class VerilogExportDialog {

    public static final String VERILOG_FILENAME_EXTENSION = "v";

    private static final FileNameExtensionFilter FILE_NAME_EXTENSION_FILTER = new FileNameExtensionFilter("Verilog file", VERILOG_FILENAME_EXTENSION);

    private VerilogExportDialog() {
    }

    public static void showExportDialog(Component parent, Design design) {
        String path = chooseFile(parent, JFileChooser.SAVE_DIALOG);
        if (path == null) {
            return;
        }
        try {
            new VerilogExporter(design, new File(path)).export();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Error while exporting: " + e);
        }
    }

    private static String chooseFile(Component parent, int dialogType) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(dialogType);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(FILE_NAME_EXTENSION_FILTER);
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            if (!path.endsWith('.' + VERILOG_FILENAME_EXTENSION)) {
                path = path + '.' + VERILOG_FILENAME_EXTENSION;
            }
            return path;
        } else {
            return null;
        }
    }

}
