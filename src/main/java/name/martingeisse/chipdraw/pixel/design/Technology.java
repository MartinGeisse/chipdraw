package name.martingeisse.chipdraw.pixel.design;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

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
    private final TechnologyBehavior behavior;

    public Technology(String id, ImmutableList<PlaneSchema> planeSchemas, TechnologyBehavior behavior) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (planeSchemas == null) {
            throw new IllegalArgumentException("planeSchemas cannot be null");
        }
        if (planeSchemas.isEmpty()) {
            throw new IllegalArgumentException("technology must have at least one plane");
        }
        this.id = id;
        this.planeSchemas = planeSchemas;
        this.behavior = new TechnologyBehavior.SafeWrapper(
            behavior == null ? TechnologyBehavior.DEFAULT : behavior);

        // make sure that none of the plane schemas is already in use
        for (PlaneSchema planeSchema : planeSchemas) {
            if (planeSchema.index >= 0) {
                throw new IllegalArgumentException("plane schema is already used by a Technology object: " + planeSchema.getName());
            }
        }

        // assign plane and material indices
        for (int i = 0; i < planeSchemas.size(); i++) {
            PlaneSchema planeSchema = planeSchemas.get(i);
            planeSchema.technology = this;
            planeSchema.index = i;
            planeSchema.initialize();
        }

    }

    public String getId() {
        return id;
    }

    public int getPlaneCount() {
        return planeSchemas.size();
    }

    public ImmutableList<PlaneSchema> getPlaneSchemas() {
        return planeSchemas;
    }

    public TechnologyBehavior getBehavior() {
        return behavior;
    }

    public boolean isPlaneSchemaValid(PlaneSchema planeSchema) {
        if (planeSchema == null) {
            throw new IllegalArgumentException("planeSchema is null");
        }
        return planeSchema.technology == this;
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
