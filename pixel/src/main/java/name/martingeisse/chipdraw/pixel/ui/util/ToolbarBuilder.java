package name.martingeisse.chipdraw.pixel.ui.util;

import name.martingeisse.chipdraw.pixel.icons.Icons;

import javax.swing.*;
import java.awt.event.ActionListener;

public final class ToolbarBuilder {

    private final JPanel toolPanel;

    public ToolbarBuilder() {
        toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.X_AXIS));
    }

    public void add(String iconFileName, ActionListener actionListener) {
        JButton button = new JButton(Icons.get(iconFileName));
        button.setFocusable(false);
        button.addActionListener(actionListener);
        toolPanel.add(button);
    }

    public JPanel build() {
        return toolPanel;
    }

}
