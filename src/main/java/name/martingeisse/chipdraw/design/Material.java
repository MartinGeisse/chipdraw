package name.martingeisse.chipdraw.design;

public final class Material {

    private final PlaneSchema planeSchema;
    private final String name;
    int localIndex;

    Material(PlaneSchema planeSchema, String name) {
        this.planeSchema = planeSchema;
        this.name = name;
    }

    public PlaneSchema getPlaneSchema() {
        return planeSchema;
    }

    public String getName() {
        return name;
    }

}
