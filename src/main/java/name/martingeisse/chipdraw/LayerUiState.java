package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.technology.PlaneSchema;
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
	private final boolean[] planesVisible;
	private int editingGlobalMaterialIndex = 0;

	public LayerUiState(Technology technology) {
		this.technology = technology;
		this.planesVisible = new boolean[technology.getPlaneCount()];
		Arrays.fill(planesVisible, true);
	}

	public TableModel getSidebarTableModel() {
		return sidebarTableModel;
	}

//region plane-oriented accessors

	public boolean isPlaneVisible(int planeIndex) {
		technology.validatePlaneIndex(planeIndex);
		return planesVisible[planeIndex];
	}

	public void setPlaneVisible(int planeIndex, boolean visible) {
		technology.validatePlaneIndex(planeIndex);
		planesVisible[planeIndex] = visible;
		sidebarTableModel.fireTableDataChanged();
	}

	public void togglePlaneVisible(int planeIndex) {
		technology.validatePlaneIndex(planeIndex);
		planesVisible[planeIndex] = !planesVisible[planeIndex];
		sidebarTableModel.fireTableDataChanged();
	}

//endregion

//region layer-oriented accessors

	public boolean isMaterialVisible(int globalMaterialIndex) {
		return isPlaneVisible(technology.getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex));
	}

	public void setMaterialVisible(int globalMaterialIndex, boolean visible) {
		setPlaneVisible(technology.getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex), visible);
	}

	public void toggleMaterialVisible(int globalMaterialIndex) {
		togglePlaneVisible(technology.getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex));
	}

//endregion

	public int getEditingGlobalMaterialIndex() {
		return editingGlobalMaterialIndex;
	}

	public void setEditingGlobalMaterialIndex(int editingGlobalMaterialIndex) {
		technology.validateGlobalMaterialIndex(editingGlobalMaterialIndex);
		int old = this.editingGlobalMaterialIndex;
		this.editingGlobalMaterialIndex = editingGlobalMaterialIndex;
		sidebarTableModel.fireTableCellUpdated(old, 0);
		sidebarTableModel.fireTableCellUpdated(editingGlobalMaterialIndex, 0);
	}

	private class SidebarTableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return technology.getGlobalMaterialCount();
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
					return rowIndex == editingGlobalMaterialIndex;

				case 1:
					return isMaterialVisible(rowIndex);

				case 2: {
					PlaneSchema planeSchema = technology.getPlaneSchemaForGlobalMaterialIndex(rowIndex);
					int localIndex = technology.getLocalMaterialIndexForGlobalMaterialIndex(rowIndex);
					return planeSchema.getMaterialNames().get(localIndex);
				}

				default:
					return null;

			}
		}

	}

}
