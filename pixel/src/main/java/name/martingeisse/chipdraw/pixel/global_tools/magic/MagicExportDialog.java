package name.martingeisse.chipdraw.pixel.global_tools.magic;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Technologies;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public final class MagicExportDialog {

	public static final String MAGIC_FILENAME_EXTENSION = "mag";

	private static final FileNameExtensionFilter FILE_NAME_EXTENSION_FILTER = new FileNameExtensionFilter("Magic design file", MAGIC_FILENAME_EXTENSION);

	private MagicExportDialog() {
	}

	public static void showExportDialog(Component parent, Design design) {
		String path = chooseFile(parent, JFileChooser.SAVE_DIALOG);
		if (path == null) {
			return;
		}
		try {
			Design convertedDesign;
			if (design.getTechnology() == Technologies.Concept.TECHNOLOGY) {
				convertedDesign = new ConceptToLibresiliconConverter(design).convert();
			} else if (design.getTechnology() == Technologies.LibreSiliconMagicScmos.TECHNOLOGY) {
				convertedDesign = design;
			} else {
				throw new IllegalArgumentException("design for Magic export must use 'concept' or 'libresilicon-magic-scmos' technology");
			}
			MagicFileIo.write(convertedDesign, new File(path), "scmos");
		} catch (ConceptToLibresiliconConverter.IncompatibilityException e) {
			JOptionPane.showMessageDialog(parent, "Design is incompatible with LibreSilicon Magic SCMOS schema: " + e);
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
			if (!path.endsWith('.' + MAGIC_FILENAME_EXTENSION)) {
				path = path + '.' + MAGIC_FILENAME_EXTENSION;
			}
			return path;
		} else {
			return null;
		}
	}

}
