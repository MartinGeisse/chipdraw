package name.martingeisse.chipdraw.technology;

public final class LayerSchema {

    private int index = -1;
    private final String name;

    public LayerSchema(String name) {
        this.name = name;
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

}
