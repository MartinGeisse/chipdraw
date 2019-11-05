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
    private final ImmutableList<PlaneSchema> planeSchemas;
    private final int globalLayerCount;
    private final int[] globalLayerIndexToPlaneIndex;
    private final int[] globalLayerIndexToLocalLayerIndex;

    public Technology(String id, ImmutableList<PlaneSchema> planeSchemas) {
        this.id = id;
        this.planeSchemas = planeSchemas;

        // make sure that none of the plane schemas is already in use
        for (PlaneSchema planeSchema : planeSchemas) {
            if (planeSchema.getIndex() >= 0) {
                throw new IllegalArgumentException("plane schema is already used by a Technology object: " + planeSchema.getName());
            }
        }

        // count number of layers across all planes
        int globalLayerCount = 0;
        for (PlaneSchema planeSchema : planeSchemas) {
            globalLayerCount += planeSchema.getLayerNames().size();
        }
        this.globalLayerCount = globalLayerCount;

        // assign plane indices; initialize mappings using global layer indices as keys
        int planeIndex = 0, globalLayerIndex = 0;
        this.globalLayerIndexToPlaneIndex = new int[globalLayerCount];
        this.globalLayerIndexToLocalLayerIndex = new int[globalLayerCount];
        for (PlaneSchema planeSchema : planeSchemas) {
            planeSchema.setIndex(planeIndex);
            for (int i = 0; i < planeSchema.getLayerNames().size(); i++) {
                globalLayerIndexToPlaneIndex[globalLayerIndex] = planeIndex;
                globalLayerIndexToLocalLayerIndex[globalLayerIndex] = i;
                globalLayerIndex++;
            }
            planeIndex++;
        }

    }

    public String getId() {
        return id;
    }

//region planes

    public int getPlaneCount() {
        return planeSchemas.size();
    }

    public boolean isPlaneIndexValid(int planeIndex) {
        return (planeIndex >= 0 && planeIndex < planeSchemas.size());
    }

    public ImmutableList<PlaneSchema> getPlaneSchemas() {
        return planeSchemas;
    }

//endregion

//region global layer indices

    public int getGlobalLayerCount() {
        return globalLayerCount;
    }

    public boolean isGlobalLayerIndexValid(int globalLayerIndex) {
        return (globalLayerIndex >= 0 && globalLayerIndex < getGlobalLayerCount());
    }

    public void validateGlobalLayerIndex(int globalLayerIndex) {
        if (!isGlobalLayerIndexValid(globalLayerIndex)) {
            throw new IllegalArgumentException("invalid global layer index: " + globalLayerIndex);
        }
    }

    public int getPlaneIndexForGlobalLayerIndex(int globalLayerIndex) {
        validateGlobalLayerIndex(globalLayerIndex);
        return globalLayerIndexToPlaneIndex[globalLayerIndex];
    }

    public PlaneSchema getPlaneSchemaForGlobalLayerIndex(int globalLayerIndex) {
        return getPlaneSchemas().get(getPlaneIndexForGlobalLayerIndex(globalLayerIndex));
    }

    public int getLocalLayerIndexForGlobalLayerIndex(int globalLayerIndex) {
        validateGlobalLayerIndex(globalLayerIndex);
        return globalLayerIndexToLocalLayerIndex[globalLayerIndex];
    }

//endregion

}
