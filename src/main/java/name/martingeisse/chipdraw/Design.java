package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.technology.TechnologyId;

import java.io.Serializable;

public final class Design implements Serializable {

    private final TechnologyId technologyId;
    private final int width;
    private final int height;
    private final ImmutableList<Layer> layers;

    public Design(TechnologyId technologyId, int width, int height) {
        this.technologyId = technologyId;
        this.width = width;
        this.height = height;
        this.layers = ImmutableList.of(
                new Layer(width, height),
                new Layer(width, height),
                new Layer(width, height)
        );
    }

    public TechnologyId getTechnologyId() {
        return technologyId;
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
