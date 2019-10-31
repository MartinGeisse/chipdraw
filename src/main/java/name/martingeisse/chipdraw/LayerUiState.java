package name.martingeisse.chipdraw;

import javax.swing.table.AbstractTableModel;

/**
 *
 */
public final class LayerUiState {

	private final boolean[] visible = new boolean[] {true, true, true};
	private int editing = 0;

	public boolean getVisible(int layer) {
		return visible[layer];
	}

	public void setVisible(int layer, boolean value) {
		visible[layer] = value;
	}

	public void toggleVisible(int layer) {
		visible[layer] = !visible[layer];
	}

	public int getEditing() {
		return editing;
	}

	public void setEditing(int editing) {
		this.editing = editing;
	}

	public class SidebarTableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return 3;
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
					return "Layer " + rowIndex;

				default:
					return null;

			}
		}

	}

}
