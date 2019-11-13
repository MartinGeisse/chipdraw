package name.martingeisse.chipdraw.design;

public final class Material {

    static final int MAX_MATERIALS = 250;
    static final byte INVALID_CODE = (byte)254;
    static final byte EMPTY_PIXEL_CODE = (byte)255;

    /**
     * Null object for empty pixels.
     */
    public static final Material NONE = new Material();

    private final PlaneSchema planeSchema;
    private final String name;
    byte code;

    private Material() {
        this.planeSchema = null;
        this.name = "none";
        this.code = EMPTY_PIXEL_CODE;
    }

    Material(PlaneSchema planeSchema, String name) {
        if (planeSchema == null) {
            throw new IllegalArgumentException("planeSchema cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.planeSchema = planeSchema;
        this.name = name;
        this.code = INVALID_CODE;
    }

    public PlaneSchema getPlaneSchema() {
        return planeSchema;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void validateNotNone() {
        if (this == NONE) {
            throw new IllegalArgumentException("passing Material.NONE not allowed here");
        }
    }

}
