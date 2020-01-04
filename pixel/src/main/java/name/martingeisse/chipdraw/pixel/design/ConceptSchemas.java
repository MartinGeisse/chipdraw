package name.martingeisse.chipdraw.pixel.design;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

public final class ConceptSchemas {

    public static final PlaneSchema PLANE_PAD;
    public static final PlaneSchema PLANE_METAL2;
    public static final PlaneSchema PLANE_METAL1;
    public static final PlaneSchema PLANE_POLY;
    public static final PlaneSchema PLANE_DIFF;
    public static final PlaneSchema PLANE_WELL;

    public static final Material MATERIAL_PAD;
    public static final Material MATERIAL_METAL2;
    public static final Material MATERIAL_VIA12;
    public static final Material MATERIAL_METAL1;
    public static final Material MATERIAL_CONTACT;
    public static final Material MATERIAL_POLY;
    public static final Material MATERIAL_NDIFF;
    public static final Material MATERIAL_PDIFF;
    public static final Material MATERIAL_NWELL;
    public static final Material MATERIAL_PWELL;

    public static final PlaneListSchema PLANE_LIST;

    static {
        PLANE_PAD = new PlaneSchema("pad");
        MATERIAL_PAD = PLANE_PAD.getMaterials().get(0);
        PLANE_METAL2 = new PlaneSchema("metal2", ImmutableList.of("metal2", "via12"));
        MATERIAL_METAL2 = PLANE_METAL2.getMaterials().get(0);
        MATERIAL_VIA12 = PLANE_METAL2.getMaterials().get(1);
        PLANE_METAL1 = new PlaneSchema("metal1", ImmutableList.of("metal1", "contact"));
        MATERIAL_METAL1 = PLANE_METAL1.getMaterials().get(0);
        MATERIAL_CONTACT = PLANE_METAL1.getMaterials().get(1);
        PLANE_POLY = new PlaneSchema("poly");
        MATERIAL_POLY = PLANE_POLY.getMaterials().get(0);
        PLANE_DIFF = new PlaneSchema("diff", ImmutableList.of("ndiff", "pdiff"));
        MATERIAL_NDIFF = PLANE_DIFF.getMaterials().get(0);
        MATERIAL_PDIFF = PLANE_DIFF.getMaterials().get(1);
        PLANE_WELL = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
        MATERIAL_NWELL = PLANE_WELL.getMaterials().get(0);
        MATERIAL_PWELL = PLANE_WELL.getMaterials().get(1);
        PLANE_LIST = new PlaneListSchema(ImmutableList.of(PLANE_PAD, PLANE_METAL2, PLANE_METAL1, PLANE_POLY, PLANE_DIFF, PLANE_WELL));
    }

    private ConceptSchemas() {
    }

    public static boolean conforms(Technology technology) {
        return technology.getPlaneListSchema() == PLANE_LIST;
    }

    public static void validateConforms(Technology technology) {
        if (!conforms(technology)) {
            throw new IllegalArgumentException("technology must use the standard 'concept' plane list schema");
        }
    }

    public static void validateConformsUserVisible(Technology technology) throws UserVisibleMessageException {
        if (!conforms(technology)) {
            throw new UserVisibleMessageException("technology must use the standard 'concept' plane list schema");
        }
    }

}
