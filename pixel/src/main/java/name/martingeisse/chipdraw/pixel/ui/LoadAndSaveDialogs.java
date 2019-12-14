package name.martingeisse.chipdraw.pixel.ui;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.NoSuchTechnologyException;
import name.martingeisse.chipdraw.pixel.design.TechnologyRepository;
import name.martingeisse.chipdraw.pixel.global_tools.magic.MagicFileIo;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public final class LoadAndSaveDialogs {

	public static final String FILE_NAME_EXTENSION = "mag";
	public static final String DOT_FILE_NAME_EXTENSION = "." + FILE_NAME_EXTENSION;

	private final TechnologyRepository technologyRepository;

	public LoadAndSaveDialogs(TechnologyRepository technologyRepository) {
		this.technologyRepository = technologyRepository;
	}

	public void showSaveDialog(Component parent, Design design) {
		File file = chooseFile(parent, FileDialog.SAVE);
		if (file == null) {
			return;
		}
		try {
			MagicFileIo.write(design, file, design.getTechnology().getId(), true);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parent, "Error while saving: " + e);
		}
	}

	public Design showLoadDialog(Component parent) throws NoSuchTechnologyException, UserVisibleMessageException {
		File file = chooseFile(parent, FileDialog.LOAD);
		if (file == null) {
			return null;
		}
		try {
			return MagicFileIo.read(file, technologyRepository);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parent, "Error while loading: " + e);
			return null;
		}
	}

	private static File chooseFile(Component parent, int mode) {
		// JFileChooser is buggy on mac -- there is no text field to enter a file name for saving

		// find frame
		Frame frame;
		while (true) {
			if (parent instanceof Frame) {
				frame = (Frame) parent;
				break;
			}
			if (parent == null) {
				throw new IllegalArgumentException("parent component is not inside a frame");
			}
			parent = parent.getParent();
		}

		// show the file chooser dialog
		FileDialog fileDialog = new FileDialog(frame);
		fileDialog.setFilenameFilter((parentFolder, filename) -> filename.endsWith(DOT_FILE_NAME_EXTENSION));
		fileDialog.setMode(mode);
		fileDialog.setMultipleMode(false);
		fileDialog.setVisible(true);

		// handle results
		File[] files = fileDialog.getFiles();
		if (files == null || files.length == 0 || files[0] == null) {
			return null;
		}
		File file = files[0];
		if (!file.getName().endsWith('.' + FILE_NAME_EXTENSION)) {
			file = new File(file.getParent(), file.getName() + '.' + FILE_NAME_EXTENSION);
		}
		return file;

	}

}
