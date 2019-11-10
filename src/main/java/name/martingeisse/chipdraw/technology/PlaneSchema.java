package name.martingeisse.chipdraw.technology;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public final class PlaneSchema {

    private int index = -1;
    private final String name;
    private final ImmutableList<Material> materials;

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

    void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public ImmutableList<Material> getMaterials() {
        return materials;
    }

}
