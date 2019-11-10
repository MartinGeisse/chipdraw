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
    private final int globalMaterialCount;
    private final int[] globalMaterialIndexToPlaneIndex;
    private final int[] globalMaterialIndexToLocalMaterialIndex;

    public Technology(String id, ImmutableList<PlaneSchema> planeSchemas) {
        if (planeSchemas.isEmpty()) {
            throw new IllegalArgumentException("technology must have at least one plane");
        }
        this.id = id;
        this.planeSchemas = planeSchemas;

        // make sure that none of the plane schemas is already in use
        for (PlaneSchema planeSchema : planeSchemas) {
            if (planeSchema.getIndex() >= 0) {
                throw new IllegalArgumentException("plane schema is already used by a Technology object: " + planeSchema.getName());
            }
        }

        // count number of materials across all planes
        int globalMaterialCount = 0;
        for (PlaneSchema planeSchema : planeSchemas) {
            globalMaterialCount += planeSchema.getMaterials().size();
        }
        this.globalMaterialCount = globalMaterialCount;

        // assign plane indices; initialize mappings using global material indices as keys as well as
        // mappings inside the Material class
        int planeIndex = 0, globalMaterialIndex = 0;
        this.globalMaterialIndexToPlaneIndex = new int[globalMaterialCount];
        this.globalMaterialIndexToLocalMaterialIndex = new int[globalMaterialCount];
        for (PlaneSchema planeSchema : planeSchemas) {
            planeSchema.setIndex(planeIndex);
            for (int i = 0; i < planeSchema.getMaterials().size(); i++) {
                planeSchema.getMaterials().get(i).setIndices(i, globalMaterialIndex);
                globalMaterialIndexToPlaneIndex[globalMaterialIndex] = planeIndex;
                globalMaterialIndexToLocalMaterialIndex[globalMaterialIndex] = i;
                globalMaterialIndex++;
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

    public void validatePlaneIndex(int planeIndex) {
        if (!isPlaneIndexValid(planeIndex)) {
            throw new IllegalArgumentException("invalid plane index: " + planeIndex);
        }
    }

    public ImmutableList<PlaneSchema> getPlaneSchemas() {
        return planeSchemas;
    }

//endregion

//region global material indices

    public int getGlobalMaterialCount() {
        return globalMaterialCount;
    }

    public boolean isGlobalMaterialIndexValid(int globalMaterialIndex) {
        return (globalMaterialIndex >= 0 && globalMaterialIndex < getGlobalMaterialCount());
    }

    public void validateGlobalMaterialIndex(int globalMaterialIndex) {
        if (!isGlobalMaterialIndexValid(globalMaterialIndex)) {
            throw new IllegalArgumentException("invalid global material index: " + globalMaterialIndex);
        }
    }

    public int getPlaneIndexForGlobalMaterialIndex(int globalMaterialIndex) {
        validateGlobalMaterialIndex(globalMaterialIndex);
        return globalMaterialIndexToPlaneIndex[globalMaterialIndex];
    }

    public PlaneSchema getPlaneSchemaForGlobalMaterialIndex(int globalMaterialIndex) {
        return getPlaneSchemas().get(getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex));
    }

    public int getLocalMaterialIndexForGlobalMaterialIndex(int globalMaterialIndex) {
        validateGlobalMaterialIndex(globalMaterialIndex);
        return globalMaterialIndexToLocalMaterialIndex[globalMaterialIndex];
    }

//endregion

}
