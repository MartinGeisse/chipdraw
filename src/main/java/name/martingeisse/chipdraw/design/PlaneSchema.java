package name.martingeisse.chipdraw.design;

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
            materials.get(i).localIndex = i;
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

}
