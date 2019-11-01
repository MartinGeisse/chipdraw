package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.technology.Technology;
import name.martingeisse.chipdraw.technology.TechnologyRepository;

import java.io.Serializable;

public final class Design implements Serializable {

    private final String technologyId;
    private transient Technology technology;
    private final int width;
    private final int height;
    private final ImmutableList<Layer> layers;

    public Design(Technology technology, int width, int height) {
        this.technologyId = technology.getId();
        this.technology = technology;
        this.width = width;
        this.height = height;
        this.layers = ImmutableList.of(
                new Layer(width, height),
                new Layer(width, height),
                new Layer(width, height)
        );
    }

    void initializeAfterDeserialization(TechnologyRepository technologyRepository) throws NoSuchTechnologyException {
        this.technology = technologyRepository.getTechnology(technologyId);
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

    public ImmutableList<Layer> getLayers() {
        return layers;
    }

}
