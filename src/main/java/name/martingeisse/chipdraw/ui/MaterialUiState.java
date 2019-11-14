package name.martingeisse.chipdraw.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.PlaneSchema;
import name.martingeisse.chipdraw.design.Technology;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class MaterialUiState {

    private final SidebarTableModel sidebarTableModel = new SidebarTableModel();
    private Technology technology;
    private ImmutableList<Material> materials;
    private Set<PlaneSchema> visiblePlanes;
    private Material editingMaterial;

    public MaterialUiState(Technology technology) {
        setTechnology(technology);
    }

    public void setTechnology(Technology technology) {
        this.technology = technology;
        this.materials = technology.getFlattenedMaterialList();
        this.visiblePlanes = new HashSet<>(technology.getPlaneSchemas());
        this.editingMaterial = materials.get(0);
        sidebarTableModel.fireTableDataChanged();
    }

    public TableModel getSidebarTableModel() {
        return sidebarTableModel;
    }

//region plane-oriented visibility accessors

    public boolean isPlaneVisible(PlaneSchema planeSchema) {
        technology.validatePlaneSchema(planeSchema);
        return visiblePlanes.contains(planeSchema);
    }

    public void setPlaneVisible(PlaneSchema planeSchema, boolean visible) {
        technology.validatePlaneSchema(planeSchema);
        if (visible) {
            visiblePlanes.add(planeSchema);
        } else {
            visiblePlanes.remove(planeSchema);
        }
        sidebarTableModel.fireTableDataChanged();
    }

    public void togglePlaneVisible(PlaneSchema planeSchema) {
        setPlaneVisible(planeSchema, !isPlaneVisible(planeSchema));
    }

//endregion

//region material-oriented visibility accessors

    public boolean isMaterialVisible(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("material cannot be null");
        }
        material.validateNotNone();
        return isPlaneVisible(material.getPlaneSchema());
    }

    public void setMaterialVisible(Material material, boolean visible) {
        if (material == null) {
            throw new IllegalArgumentException("material cannot be null");
        }
        material.validateNotNone();
        setPlaneVisible(material.getPlaneSchema(), visible);
    }

    public void toggleMaterialVisible(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("material cannot be null");
        }
        material.validateNotNone();
        togglePlaneVisible(material.getPlaneSchema());
    }

//endregion

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
        outer: {
            for (int i = 0; i < planeGroups.size(); i++) {
                if (visiblePlanes.contains(planeGroups.get(i).get(0))) {
                    currentPlaneGroupIndex = i;
                    break outer;
                }
            }
            currentPlaneGroupIndex = - 1;
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

//region current editing material

    public Material getEditingMaterial() {
        return editingMaterial;
    }

    public void setEditingMaterial(Material editingMaterial) {
        if (editingMaterial == null) {
            throw new IllegalArgumentException("editingMaterial cannot be null");
        }
        editingMaterial.validateNotNone();
        technology.validatePlaneSchema(editingMaterial.getPlaneSchema());
        this.editingMaterial = editingMaterial;
        sidebarTableModel.fireTableDataChanged();
    }

//endregion

    private class SidebarTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return materials.size();
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
            if (rowIndex < 0 || rowIndex >= materials.size()) {
                return null;
            }
            switch (columnIndex) {

                case 0:
                    return materials.get(rowIndex) == editingMaterial;

                case 1:
                    return visiblePlanes.contains(materials.get(rowIndex).getPlaneSchema());

                case 2:
                    return materials.get(rowIndex).getName();

                default:
                    return null;

            }
        }

    }

    public void onClick(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < materials.size()) {
            if (columnIndex == 1) {
                toggleMaterialVisible(materials.get(rowIndex));
            } else {
                setEditingMaterial(materials.get(rowIndex));
            }
        }
    }

}
