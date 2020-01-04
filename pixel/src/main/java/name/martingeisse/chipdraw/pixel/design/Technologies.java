package name.martingeisse.chipdraw.pixel.design;

import com.google.common.collect.ImmutableList;

public final class Technologies {

    /**
     * Minimal pseudo-technology that is meant to design a cell concept. If all rules are respected (and those should
     * be DRC-checked) then an actual technology-specific layout can in theory be generated from this one. This is
     * "halfway SCMOS": The numeric values used in the DRC are still technology-specific, but the way the design is
     * built (i.e. the planes and materials) are technology-independent.
     * <p>
     * Vias and contacts are drawn as special materials in the *upper* connected plane. That is, well taps, diffusion
     * contacts and poly contacts are drawn in the metal1 plane using a special "contact" material instead of the
     * normal metal1 material. Metal1-to-metal2 vias are drawn in metal2 using a special via12 material, and so on.
     * Using a material instead of a plane simplifies things (no DRC needed between the via and the upper connected
     * plane), and using the upper connected plane instead of the lower one avoids complexity due to the lowest
     * planes (wells / diffusion / poly) being multiple planes with multiple materials each.
     */
    public static final class Concept {

        public static final Technology TECHNOLOGY;

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
            TechnologyBehavior technologyBehavior = new TechnologyBehavior() {
                @Override
                public ImmutableList<ImmutableList<PlaneSchema>> getPlaneGroups() {
                    return ImmutableList.of(
                            ImmutableList.of(PLANE_PAD, PLANE_METAL2),
                            ImmutableList.of(PLANE_METAL2, PLANE_METAL1),
                            ImmutableList.of(PLANE_METAL1, PLANE_POLY, PLANE_DIFF, PLANE_WELL),
                            ImmutableList.of(PLANE_POLY, PLANE_DIFF, PLANE_WELL)
                    );
                }
            };
            TECHNOLOGY = new Technology("concept",
                    new PlaneListSchema(ImmutableList.of(PLANE_PAD, PLANE_METAL2, PLANE_METAL1, PLANE_POLY, PLANE_DIFF, PLANE_WELL)),
                    technologyBehavior);
        }

        private Concept() {
        }

    }

    /**
     * This closely resembles the LibreSilicon designs in Magic.
     */
    public static final class LibreSiliconMagicScmos {

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
            TECHNOLOGY = new Technology("libresilicon-magic-scmos",
                    new PlaneListSchema(ImmutableList.of(PLANE_WELL, PLANE_ACTIVE, PLANE_METAL1, PLANE_METAL2)),
                    null);
        }

        private LibreSiliconMagicScmos() {
        }

    }

    private Technologies() {
    }

}
