package name.martingeisse.chipdraw.pnr.ui.util;

import name.martingeisse.chipdraw.pnr.util.UserVisibleMessageException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 *
 */
public final class MenuBarBuilder {

	private final JMenuBar menuBar = new JMenuBar();
	private JMenu menu = null;

	public void addMenu(String name) {
		menu = new JMenu(name);
		menuBar.add(menu);
	}

	public void add(String name, UiRunnable runnable) {
		add(name, event -> {
			try {
				runnable.run();
			} catch (UserVisibleMessageException e) {
				JOptionPane.showMessageDialog(menuBar, e.getMessage());
			}
		});
	}

	public void add(String name, ActionListener actionListener) {
		checkHasMenu();
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(actionListener);
		menu.add(item);
	}

	public void addExternalLink(String name, String url) {
		add(name, () -> {
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URI(url));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(menuBar, "error opening URL: " + e.getMessage());
				}
			}
		});
	}

	public void addSeparator() {
		checkHasMenu();
		menu.addSeparator();
	}

	private void checkHasMenu() {
		if (menu == null) {
			throw new IllegalStateException("no menu added yet");
		}
	}

	public JMenuBar build() {
		return menuBar;
	}

}
