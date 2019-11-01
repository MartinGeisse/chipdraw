package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.technology.Technology;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.Arrays;

/**
 *
 */
public final class LayerUiState {

	private final Technology technology;
	private final SidebarTableModel sidebarTableModel = new SidebarTableModel();
	private final boolean[] visible;
	private int editing = 0;

	public LayerUiState(Technology technology) {
		this.technology = technology;
		this.visible = new boolean[technology.getLayerSchemas().size()];
		Arrays.fill(visible, true);
	}

	public TableModel getSidebarTableModel() {
		return sidebarTableModel;
	}

	public boolean getVisible(int layer) {
		technology.validateLayerIndex(layer);
		return visible[layer];
	}

	public void setVisible(int layer, boolean value) {
		technology.validateLayerIndex(layer);
		visible[layer] = value;
		sidebarTableModel.fireTableCellUpdated(layer, 1);
	}

	public void toggleVisible(int layer) {
		technology.validateLayerIndex(layer);
		visible[layer] = !visible[layer];
		sidebarTableModel.fireTableCellUpdated(layer, 1);
	}

	public int getEditing() {
		return editing;
	}

	public void setEditing(int editing) {
		technology.validateLayerIndex(editing);
		int old = this.editing;
		this.editing = editing;
		sidebarTableModel.fireTableCellUpdated(old, 0);
		sidebarTableModel.fireTableCellUpdated(editing, 0);
	}

	private class SidebarTableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return technology.getLayerSchemas().size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {

				case 0:
					return "editing";

				case 1:
					return "visible";

				case 2:
					return "name";

				default:
					return super.getColumnName(column);

			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return (columnIndex < 2) ? Boolean.TYPE : String.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {

				case 0:
					return rowIndex == editing;

				case 1:
					return visible[rowIndex];

				case 2:
					return technology.getLayerSchemas().get(rowIndex).getName();

				default:
					return null;

			}
		}

	}

}
