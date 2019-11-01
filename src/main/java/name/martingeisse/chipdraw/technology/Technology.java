package name.martingeisse.chipdraw.technology;

import com.google.common.collect.ImmutableList;

/**
 * Using a simple string / identifier as the technology ID: My original plan was to use an identifier plus a hash code,
 * to detect version changes or other differences in technologies using the same identifier on different machines.
 * However, this is not useful, because most of what defines a technology is in the code, and we can't compute a hash
 * from that in a useful way. Even versioning of a technology can't be done that way. So I'm back to a simple identifier.
 * We should be able to detect changes mostly through the DRC, especially when we include "hint" rules that tell the
 * user, for example, when a trace is wider than necessary. All other versioning should be solved through process and by
 * using a VCS.
 */
public final class Technology {

    private final String id;
    private final ImmutableList<LayerSchema> layerSchemas;

    public Technology(String id, ImmutableList<LayerSchema> layerSchemas) {
        this.id = id;
        this.layerSchemas = layerSchemas;
        for (LayerSchema layerSchema : layerSchemas) {
            if (layerSchema.getIndex() >= 0) {
                throw new IllegalArgumentException("layer schema is already used by a Technology object: " + layerSchema.getName());
            }
        }
        int index = 0;
        for (LayerSchema layerSchema : layerSchemas) {
            layerSchema.setIndex(index);
            index++;
        }
    }

    public String getId() {
        return id;
    }

    public int getLayerCount() {
        return layerSchemas.size();
    }

    public boolean isLayerIndexValid(int layerIndex) {
        return (layerIndex >= 0 && layerIndex < layerSchemas.size());
    }

    public void validateLayerIndex(int layerIndex) {
        if (!isLayerIndexValid(layerIndex)) {
            throw new IllegalArgumentException("invalid layer index: " + layerIndex);
        }
    }

    public ImmutableList<LayerSchema> getLayerSchemas() {
        return layerSchemas;
    }

}
