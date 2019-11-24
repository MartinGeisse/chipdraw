package name.martingeisse.chipdraw.pnr.ui;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pnr.design.Material;
import name.martingeisse.chipdraw.pnr.design.PlaneSchema;
import name.martingeisse.chipdraw.pnr.design.Technology;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class MaterialUiState {

    TODO REVIEW

    private final SidebarTableModel sidebarTableModel = new SidebarTableModel();
    private int totalPlaneCount;
    private boolean[] planeVisible;
    private int editingPlane;

    public MaterialUiState(int totalPlaneCount) {
        setTotalPlaneCount(totalPlaneCount);
    }

    public int getTotalPlaneCount() {
        return totalPlaneCount;
    }

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
        ImmutableList<ImmutableList<PlaneSchema>> planeGroups = technology.getBehavior().getPlaneGroups();
        if (planeGroups.isEmpty()) {
            return;
        }

        /* TODO this will not work because the same plane schema can be part of multiple plane groups! */
        final int currentPlaneGroupIndex;
        outer:
        {
            for (int i = 0; i < planeGroups.size(); i++) {
                if (visiblePlanes.contains(planeGroups.get(i).get(0))) {
                    currentPlaneGroupIndex = i;
                    break outer;
                }
            }
            currentPlaneGroupIndex = -1;
        }

        final int newPlaneGroupIndex;
        if (currentPlaneGroupIndex < 0) {
            if (delta < 0) {
                newPlaneGroupIndex = 0;
            } else {
                newPlaneGroupIndex = planeGroups.size() - 1;
            }
        } else if (delta < 0) {
            newPlaneGroupIndex = Math.max(currentPlaneGroupIndex + delta, 0);
        } else {
            newPlaneGroupIndex = Math.min(currentPlaneGroupIndex + delta, planeGroups.size() - 1);
        }

        visiblePlanes.clear();
        visiblePlanes.addAll(planeGroups.get(newPlaneGroupIndex));
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
