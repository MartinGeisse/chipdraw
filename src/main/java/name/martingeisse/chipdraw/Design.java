package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.technology.PlaneSchema;
import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.technology.Technology;
import name.martingeisse.chipdraw.technology.TechnologyRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class Design implements Serializable {

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

}
