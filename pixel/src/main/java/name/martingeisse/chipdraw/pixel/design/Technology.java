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
    private final PlaneListSchema planeListSchema;
    private final TechnologyBehavior behavior;

    public Technology(String id, PlaneListSchema planeListSchema, TechnologyBehavior behavior) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (planeListSchema == null) {
            throw new IllegalArgumentException("planeListSchema cannot be null");
        }
        this.id = id;
        this.planeListSchema = planeListSchema;
        this.behavior = new TechnologyBehavior.SafeWrapper(behavior == null ? TechnologyBehavior.DEFAULT : behavior);
    }

    public String getId() {
        return id;
    }

    public PlaneListSchema getPlaneListSchema() {
        return planeListSchema;
    }

    public int getPlaneCount() {
        return planeListSchema.getPlaneCount();
    }

    public ImmutableList<PlaneSchema> getPlaneSchemas() {
        return planeListSchema.getPlaneSchemas();
    }

    public TechnologyBehavior getBehavior() {
        return behavior;
    }

    public boolean isPlaneSchemaValid(PlaneSchema planeSchema) {
        return planeListSchema.isPlaneSchemaValid(planeSchema);
    }

    public void validatePlaneSchema(PlaneSchema planeSchema) {
        planeListSchema.validatePlaneSchema(planeSchema);
    }

    public ImmutableList<Material> getFlattenedMaterialList() {
        return planeListSchema.getFlattenedMaterialList();
    }

}
