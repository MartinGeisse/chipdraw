package name.martingeisse.chipdraw.pixel.design;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * A schema for a list of planes, which in turn is a fixed list of {@link PlaneSchema} objects. This class is used to
 * share the plane information across multiple {@link Technology}s.
 */
public final class PlaneListSchema {

    private final ImmutableList<PlaneSchema> planeSchemas;

    public PlaneListSchema(ImmutableList<PlaneSchema> planeSchemas) {
        if (planeSchemas == null || planeSchemas.isEmpty()) {
            throw new IllegalArgumentException("planeSchemas cannot be null or empty");
        }
        this.planeSchemas = planeSchemas;

        // make sure that none of the plane schemas is already in use in another PlaneListSchema
        for (PlaneSchema planeSchema : planeSchemas) {
            if (planeSchema.index >= 0) {
                throw new IllegalArgumentException("plane schema is already used by a PlaneListSchema object: " + planeSchema.getName());
            }
        }

        // assign plane and material indices
        for (int i = 0; i < planeSchemas.size(); i++) {
            PlaneSchema planeSchema = planeSchemas.get(i);
            planeSchema.planeListSchema = this;
            planeSchema.index = i;
            planeSchema.initialize();
        }

    }

    public int getPlaneCount() {
        return planeSchemas.size();
    }

    public ImmutableList<PlaneSchema> getPlaneSchemas() {
        return planeSchemas;
    }

    public boolean isPlaneSchemaValid(PlaneSchema planeSchema) {
        if (planeSchema == null) {
            throw new IllegalArgumentException("planeSchema is null");
        }
        return planeSchema.planeListSchema == this;
    }

    public void validatePlaneSchema(PlaneSchema planeSchema) {
        if (!isPlaneSchemaValid(planeSchema)) {
            throw new IllegalArgumentException("unknown plane schema: " + planeSchema);
        }
    }

    public ImmutableList<Material> getFlattenedMaterialList() {
        List<Material> result = new ArrayList<>();
        for (PlaneSchema planeSchema : planeSchemas) {
            result.addAll(planeSchema.getMaterials());
        }
        return ImmutableList.copyOf(result);
    }

}
