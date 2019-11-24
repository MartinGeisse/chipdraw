package name.martingeisse.chipdraw.pixel.design;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public final class PlaneSchema {

    private final String name;
    private final ImmutableList<Material> materials;
    Technology technology;
    int index = -1;

    /**
     * Constructor for a single-material plane.
     */
    public PlaneSchema(String name) {
        this(name, ImmutableList.of(name));
    }

    /**
     * Constructor for a multi-material plane.
     */
    public PlaneSchema(String name, ImmutableList<String> materialNames) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (materialNames == null) {
            throw new IllegalArgumentException("materialNames cannot be null");
        }
        if (materialNames.isEmpty()) {
            throw new IllegalArgumentException("plane must have at least one material");
        }
        this.name = name;
        List<Material> materials = new ArrayList<>();
        for (String materialName : materialNames) {
            materials.add(new Material(this, materialName));
        }
        this.materials = ImmutableList.copyOf(materials);
    }

    void initialize() {
        for (int i = 0; i < materials.size(); i++) {
            materials.get(i).code = (byte)i;
        }
    }

    public String getName() {
        return name;
    }

    public ImmutableList<Material> getMaterials() {
        return materials;
    }

    public Technology getTechnology() {
        return technology;
    }

    public boolean isMaterialValid(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("material is null");
        }
        return material == Material.NONE || material.getPlaneSchema() == this;
    }

    public void validateMaterial(Material material) {
        if (!isMaterialValid(material)) {
            throw new IllegalArgumentException("unknown material: " + material);
        }
    }

    @Override
    public String toString() {
        return name;
    }

}
