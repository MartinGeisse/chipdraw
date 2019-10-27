package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;

public final class Design implements Serializable {

    private final int width;
    private final int height;
    private final ImmutableList<Layer> layers;

    public Design(int width, int height) {
        this.width = width;
        this.height = height;
        this.layers = ImmutableList.of(
                new Layer(width, height),
                new Layer(width, height),
                new Layer(width, height)
        );
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
