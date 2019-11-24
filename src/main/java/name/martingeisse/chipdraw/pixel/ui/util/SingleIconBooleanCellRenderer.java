package name.martingeisse.chipdraw.pixel.ui.util;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class SingleIconBooleanCellRenderer extends JLabel implements TableCellRenderer {

    private final Icon onTrueIcon;

    public SingleIconBooleanCellRenderer(Icon onTrueIcon) {
        this.onTrueIcon = onTrueIcon;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        boolean visible = (value != null && (Boolean) value);
        setIcon(visible ? onTrueIcon : null);
        return this;
    }

}
