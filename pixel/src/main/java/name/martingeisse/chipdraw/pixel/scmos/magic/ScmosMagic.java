package name.martingeisse.chipdraw.pixel.scmos.magic;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.PlaneListSchema;
import name.martingeisse.chipdraw.pixel.design.PlaneSchema;
import name.martingeisse.chipdraw.pixel.design.Technology;

/**
 * This closely resembles the SCMOS designs in Magic.
 */
public final class ScmosMagic {

    public static final Technology TECHNOLOGY;

    public static final PlaneSchema PLANE_WELL;
    public static final PlaneSchema PLANE_ACTIVE;
    public static final PlaneSchema PLANE_METAL1;
    public static final PlaneSchema PLANE_METAL2;

    public static final Material MATERIAL_NWELL;
    public static final Material MATERIAL_PWELL;
    public static final Material MATERIAL_POLYSILICON;
    public static final Material MATERIAL_NDIFFUSION;
    public static final Material MATERIAL_PDIFFUSION;
    public static final Material MATERIAL_NTRANSISTOR;
    public static final Material MATERIAL_PTRANSISTOR;
    public static final Material MATERIAL_POLYCONTACT;
    public static final Material MATERIAL_NDCONTACT;
    public static final Material MATERIAL_PDCONTACT;
    public static final Material MATERIAL_NSUBSTRATENCONTACT;
    public static final Material MATERIAL_PSUBSTRATEPCONTACT;
    public static final Material MATERIAL_METAL1;
    public static final Material MATERIAL_M2CONTACT;
    public static final Material MATERIAL_METAL2;
    public static final Material MATERIAL_PAD;

    static {
        PLANE_WELL = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
        MATERIAL_NWELL = PLANE_WELL.getMaterials().get(0);
        MATERIAL_PWELL = PLANE_WELL.getMaterials().get(1);
        PLANE_ACTIVE = new PlaneSchema("active", ImmutableList.of(
            "polysilicon",
            "ndiffusion", "pdiffusion",
            "ntransistor", "ptransistor",
            "polycontact",
            "ndcontact", "pdcontact",
            "nsubstratencontact", "psubstratepcontact"
        ));
        MATERIAL_POLYSILICON = PLANE_ACTIVE.getMaterials().get(0);
        MATERIAL_NDIFFUSION = PLANE_ACTIVE.getMaterials().get(1);
        MATERIAL_PDIFFUSION = PLANE_ACTIVE.getMaterials().get(2);
        MATERIAL_NTRANSISTOR = PLANE_ACTIVE.getMaterials().get(3);
        MATERIAL_PTRANSISTOR = PLANE_ACTIVE.getMaterials().get(4);
        MATERIAL_POLYCONTACT = PLANE_ACTIVE.getMaterials().get(5);
        MATERIAL_NDCONTACT = PLANE_ACTIVE.getMaterials().get(6);
        MATERIAL_PDCONTACT = PLANE_ACTIVE.getMaterials().get(7);
        MATERIAL_NSUBSTRATENCONTACT = PLANE_ACTIVE.getMaterials().get(8);
        MATERIAL_PSUBSTRATEPCONTACT = PLANE_ACTIVE.getMaterials().get(9);
        PLANE_METAL1 = new PlaneSchema("metal1", ImmutableList.of("metal1", "m2contact"));
        MATERIAL_METAL1 = PLANE_METAL1.getMaterials().get(0);
        MATERIAL_M2CONTACT = PLANE_METAL1.getMaterials().get(1);
        PLANE_METAL2 = new PlaneSchema("metal2", ImmutableList.of("metal2", "pad"));
        MATERIAL_METAL2 = PLANE_METAL2.getMaterials().get(0);
        MATERIAL_PAD = PLANE_METAL2.getMaterials().get(1);
        TECHNOLOGY = new Technology("ScmosMagic",
            new PlaneListSchema(ImmutableList.of(PLANE_WELL, PLANE_ACTIVE, PLANE_METAL1, PLANE_METAL2)),
            null);
    }

    private ScmosMagic() {
    }

}
