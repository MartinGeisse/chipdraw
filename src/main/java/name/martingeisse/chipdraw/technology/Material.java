package name.martingeisse.chipdraw.technology;

public final class Material {

    private final PlaneSchema planeSchema;
    private final String name;
    private int localIndex;
    private int globalIndex;

    Material(PlaneSchema planeSchema, String name) {
        this.planeSchema = planeSchema;
        this.name = name;
    }

    void setIndices(int local, int global) {
        this.localIndex = local;
        this.globalIndex = global;
    }

    public PlaneSchema getPlaneSchema() {
        return planeSchema;
    }

    public String getName() {
        return name;
    }

    public int getLocalIndex() {
        return localIndex;
    }

    public int getGlobalIndex() {
        return globalIndex;
    }

}
