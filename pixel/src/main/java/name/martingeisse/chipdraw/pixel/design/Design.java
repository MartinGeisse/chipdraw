package name.martingeisse.chipdraw.pixel.design;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.util.RectangularSize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class Design implements Serializable, RectangularSize {

    private static final long serialVersionUID = 1;

    private final String technologyId;
    private transient Technology technology;
    private final int width;
    private final int height;
    private final ImmutableList<Plane> planes;

    public Design(Technology technology, int width, int height) {
        this.technologyId = technology.getId();
        this.technology = technology;
        this.width = width;
        this.height = height;

        List<Plane> planes = new ArrayList<>();
        for (PlaneSchema planeSchema : technology.getPlaneSchemas()) {
            planes.add(new Plane(planeSchema, width, height));
        }
        this.planes = ImmutableList.copyOf(planes);
    }

    public Design(Design original) {
        this(original.getTechnology(), original.getWidth(), original.getHeight());
        for (int i = 0; i < planes.size(); i++) {
            planes.get(i).copyFrom(original.getPlanes().get(i));
        }
    }

    void initializeAfterDeserialization(TechnologyRepository technologyRepository) throws NoSuchTechnologyException {
        this.technology = technologyRepository.getTechnology(technologyId);
        if (technology.getPlaneSchemas().size() != planes.size()) {
            throw new RuntimeException("number of planes in this technology has changed");
        }
        for (int i = 0; i < planes.size(); i++) {
            planes.get(i).initializeAfterDeserialization(technology.getPlaneSchemas().get(i));
        }
    }

    public Technology getTechnology() {
        return technology;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImmutableList<Plane> getPlanes() {
        return planes;
    }

    public Plane getPlane(PlaneSchema schema) {
        technology.validatePlaneSchema(schema);
        return planes.get(schema.index);
    }

    public void copyFrom(Design source, int sourceX, int sourceY, int destinationX, int destinationY, int rectangleWidth, int rectangleHeight) {
        if (source.getTechnology() != getTechnology()) {
            throw new IllegalArgumentException("cannot copy from design with different technology");
        }
        source.validateSubRectangle(sourceX, sourceY, rectangleWidth, rectangleHeight);
        validateSubRectangle(destinationX, destinationY, rectangleWidth, rectangleHeight);
        for (int i = 0; i < planes.size(); i++) {
            planes.get(i).copyFrom(source.getPlanes().get(i), sourceX, sourceY, destinationX, destinationY, rectangleWidth, rectangleHeight);
        }
    }

    public Design createCopyOfRectangle(int x, int y, int width, int height) {
        Design copy = new Design(technology, width, height);
        copy.copyFrom(this, x, y, 0, 0, width, height);
        return copy;
    }

    public static boolean pixelEquals(Design design1, Design design2, int x, int y) {
        if (design1.getTechnology() != design2.getTechnology()) {
            throw new IllegalArgumentException("designs use different technologies");
        }
        for (int i = 0; i < design1.getPlanes().size(); i++) {
            if (design1.getPlanes().get(i).getPixel(x, y) != design2.getPlanes().get(i).getPixel(x, y)) {
                return false;
            }
        }
        return true;
    }

    public static boolean rectangleEquals(Design design1, Design design2, int x, int y, int width, int height) {
        if (design1.getTechnology() != design2.getTechnology()) {
            throw new IllegalArgumentException("designs use different technologies");
        }
        for (int i = 0; i < design1.getPlanes().size(); i++) {
            if (!Plane.rectangleEquals(design1.getPlanes().get(i), design2.getPlanes().get(i), x, y, width, height)) {
                return false;
            }
        }
        return true;
    }

}
