package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;

public final class Design implements Serializable {

    private final String technologyId;
    private final int width;
    private final int height;
    private final ImmutableList<Layer> layers;

    public Design(String technologyId, int width, int height) {
        this.technologyId = technologyId;
        this.width = width;
        this.height = height;
        this.layers = ImmutableList.of(
                new Layer(width, height),
                new Layer(width, height),
                new Layer(width, height)
        );
    }

    public String getTechnologyId() {
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
