package name.martingeisse.chipdraw.pnr.ui;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.Arrays;

/**
 *
 */
public final class PlaneUiState {

    private final SidebarTableModel sidebarTableModel = new SidebarTableModel();
    private int totalPlaneCount;
    private boolean[] planeVisible;
    private int editingPlane;

    public PlaneUiState(int totalPlaneCount) {
        setTotalPlaneCount(totalPlaneCount);
    }

    public int getTotalPlaneCount() {
        return totalPlaneCount;
    }

    TODO routing planes

    public void setTotalPlaneCount(int totalPlaneCount) {
        if (totalPlaneCount <= 0) {
            throw new IllegalArgumentException("total plane count must be positive");
        }
        this.totalPlaneCount = totalPlaneCount;
        this.planeVisible = new boolean[totalPlaneCount];
        Arrays.fill(planeVisible, true);
        this.editingPlane = 0;
    }

    public boolean isPlaneIndexValid(int planeIndex) {
        return planeIndex >= 0 && planeIndex < totalPlaneCount;
    }

    private void validatePlaneIndex(int planeIndex) {
        if (!isPlaneIndexValid(planeIndex)) {
            throw new IllegalArgumentException("invalid plane index: " + planeIndex);
        }
    }

    public boolean isPlaneVisible(int planeIndex) {
        validatePlaneIndex(planeIndex);
        return planeVisible[planeIndex];
    }

    public void setPlaneVisible(int planeIndex, boolean visible) {
        validatePlaneIndex(planeIndex);
        planeVisible[planeIndex] = visible;
    }

    public void togglePlaneVisible(int planeIndex) {
        validatePlaneIndex(planeIndex);
        planeVisible[planeIndex] = !planeVisible[planeIndex];
    }

    public int getEditingPlane() {
        return editingPlane;
    }

    public void setEditingPlane(int editingPlane) {
        validatePlaneIndex(editingPlane);
        this.editingPlane = editingPlane;
    }

    public TableModel getSidebarTableModel() {
        return sidebarTableModel;
    }

//region visibility up/down

    public void moveVisibilityUp() {
        shiftVisibility(-1);
    }

    public void moveVisibilityDown() {
        shiftVisibility(1);
    }

    private void shiftVisibility(int delta) {
        // TODO does this have any meaning in the routing editor?
    }

//endregion

    private class SidebarTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return totalPlaneCount;
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
            if (!isPlaneIndexValid(rowIndex)) {
                return null;
            }
            switch (columnIndex) {

                case 0:
                    return rowIndex == editingPlane;

                case 1:
                    return planeVisible[rowIndex];

                case 2:
                    return "plane " + rowIndex;

                default:
                    return null;

            }
        }

    }

    public void onClick(int rowIndex, int columnIndex) {
        if (isPlaneIndexValid(rowIndex)) {
            if (columnIndex == 1) {
                togglePlaneVisible(rowIndex);
            } else {
                setEditingPlane(rowIndex);
            }
        }
    }

}
