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
	private final boolean[] visible;
	private int editing = 0;

	public LayerUiState(Technology technology) {
		this.technology = technology;
		this.visible = new boolean[technology.getPlaneSchemas().size()];
		Arrays.fill(visible, true);
	}

	public TableModel getSidebarTableModel() {
		return sidebarTableModel;
	}

//region plane-oriented accessors

	public boolean isPlaneVisible(int planeIndex) {
		return false; // TODO
	}

	public void setPlaneVisible(int planeIndex) {
		// TODO
	}

	public void togglePlaneVisible(int planeIndex) {
		// TODO
	}

//endregion

//region layer-oriented accessors

	public boolean isLayerVisible(int globalLayerIndex) {
		return isPlaneVisible(technology.getPlaneIndexForGlobalLayerIndex(globalLayerIndex));
	}

	public void setLayerVisible(int globalLayerIndex) {
		setPlaneVisible(technology.getPlaneIndexForGlobalLayerIndex(globalLayerIndex));
	}

	public void toggleLayerVisible(int globalLayerIndex) {
		togglePlaneVisible(technology.getPlaneIndexForGlobalLayerIndex(globalLayerIndex));
	}

//endregion

	public boolean getLayerVisible(int layer) {
		technology.validateGlobalLayerIndex(layer);
		return visible[layer];
	}

	public void setLayerVisible(int layer, boolean value) {
		technology.validateGlobalLayerIndex(layer);
		visible[layer] = value;
		sidebarTableModel.fireTableCellUpdated(layer, 1);
	}

	public void toggleVisible(int layer) {
		technology.validateGlobalLayerIndex(layer);
		visible[layer] = !visible[layer];
		sidebarTableModel.fireTableCellUpdated(layer, 1);
	}

	// TODO rename to getEditingLayer
	public int getEditing() {
		return editing;
	}

	// TODO rename to setEditingLayer
	public void setEditing(int editing) {
		technology.validateGlobalLayerIndex(editing);
		int old = this.editing;
		this.editing = editing;
		sidebarTableModel.fireTableCellUpdated(old, 0);
		sidebarTableModel.fireTableCellUpdated(editing, 0);
	}

	private class SidebarTableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return technology.getGlobalLayerCount();
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
					return rowIndex == editing; // TODO

				case 1:
					return isLayerVisible(rowIndex);

				case 2: {
					PlaneSchema planeSchema = technology.getPlaneSchemaForGlobalLayerIndex(rowIndex);
					int localIndex = technology.getLocalLayerIndexForGlobalLayerIndex(rowIndex);
					return planeSchema.getLayerNames().get(localIndex);
				}

				default:
					return null;

			}
		}

	}

}
