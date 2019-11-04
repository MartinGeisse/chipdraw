package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.technology.LayerSchema;
import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.technology.Technology;
import name.martingeisse.chipdraw.technology.TechnologyRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Note: Chipdraw works with a list of layers with boolean-typed pixels. Magic sometimes works like this but sometimes
 * it works with fewer layers and enum-typed pixels -- I don't fully understand *when* the distinction is made, but
 * both systems work in obvious ways when abiding to the following rules:
 * - each enum-typed pixel can be "empty" instead of any enum value
 * - each boolean layer corresponds to a combination of an enum-typed layer and an enum value
 * - each enum-typed layer corresponds to a set of boolean layers, one per value
 * - an enum pixel is empty when no enum-type-associated boolean pixel is true
 * - when an enum pixel is nonempty, all enum-type-associated pixels are false except for the one for that enum value
 *
 * For example, when writing to a boolean layer, the previously set boolean pixel for the same enum-type must be unset.
 * In concrete terms, this means:
 * - writing a pwell pixel clears the corresponding nwell pixel and vice versa
 * - writing a pdiff pixel clears the corresponding ndigg pixel and vice versa
 */
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

        List<Layer> layers = new ArrayList<>();
        for (LayerSchema layerSchema : technology.getLayerSchemas()) {
            layers.add(new Layer(layerSchema, width, height));
        }
        this.layers = ImmutableList.copyOf(layers);
    }

    void initializeAfterDeserialization(TechnologyRepository technologyRepository) throws NoSuchTechnologyException {
        this.technology = technologyRepository.getTechnology(technologyId);
        if (technology.getLayerSchemas().size() != layers.size()) {
            throw new RuntimeException("number of layers in this technology has changed");
        }
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).initializeAfterDeserialization(technology.getLayerSchemas().get(i));
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

    public ImmutableList<Layer> getLayers() {
        return layers;
    }

}
