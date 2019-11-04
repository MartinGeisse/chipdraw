package name.martingeisse.chipdraw.technology;

import com.google.common.collect.ImmutableList;

public final class PlaneSchema {

    private int index = -1;
    private final String name;
    private final ImmutableList<String> layerNames;

    /**
     * Constructor for a single-layer plane.
     */
    public PlaneSchema(String name) {
        this(name, ImmutableList.of(name));
    }

    /**
     * Constructor for a multi-layer plane.
     */
    public PlaneSchema(String name, ImmutableList<String> layerNames) {
        this.name = name;
        this.layerNames = layerNames;
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

    public ImmutableList<String> getLayerNames() {
        return layerNames;
    }

}
