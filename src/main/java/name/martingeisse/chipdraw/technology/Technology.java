package name.martingeisse.chipdraw.technology;

public final class Technology {

    private final TechnologyId id;
    private final int layerCount;

    public Technology(TechnologyId id, int layerCount) {
        this.id = id;
        this.layerCount = layerCount;
    }

    public TechnologyId getId() {
        return id;
    }

    public int getLayerCount() {
        return layerCount;
    }
}
