package name.martingeisse.chipdraw.design;

import com.google.common.collect.ImmutableList;

public final class Technologies {

    /**
     * Minimal pseudo-technology that is meant to design a cell concept. If all rules are respected (and those should
     * be DRC-checked) then an actual technology-specific layout can in theory be generated from this one. This is
     * "halfway SCMOS": The numeric values used in the DRC are still technology-specific, but the way the design is
     * built (i.e. the planes and materials) are technology-independent.
     *
     * Vias and contacts are drawn as special materials in the *upper* connected plane. That is, well taps, diffusion
     * contacts and poly contacts are drawn in the metal1 plane using a special "contact" material instead of the
     * normal metal1 material. Metal1-to-metal2 vias are drawn in metal2 using a special via12 material, and so on.
     * Using a material instead of a plane simplifies things (no DRC needed between the via and the upper connected
     * plane), and using the upper connected plane instead of the lower one avoids complexity due to the lowest
     * planes (wells / diffusion / poly) being multiple planes with multiple materials each.
     */
    public static final class Concept {

        public static final Technology TECHNOLOGY;

        public static final PlaneSchema PLANE_WELL;
        public static final PlaneSchema PLANE_DIFF;
        public static final PlaneSchema PLANE_POLY;
        public static final PlaneSchema PLANE_METAL1;
        public static final PlaneSchema PLANE_METAL2;
        public static final PlaneSchema PLANE_PAD;

        public static final Material MATERIAL_NWELL;
        public static final Material MATERIAL_PWELL;
        public static final Material MATERIAL_NDIFF;
        public static final Material MATERIAL_PDIFF;
        public static final Material MATERIAL_POLY;
        public static final Material MATERIAL_CONTACT;
        public static final Material MATERIAL_METAL1;
        public static final Material MATERIAL_VIA12;
        public static final Material MATERIAL_METAL2;
        public static final Material MATERIAL_PAD;

        private Concept() {
        }

        static {
            PLANE_WELL = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
            MATERIAL_NWELL = PLANE_WELL.getMaterials().get(0);
            MATERIAL_PWELL = PLANE_WELL.getMaterials().get(1);
            PLANE_DIFF = new PlaneSchema("diff", ImmutableList.of("ndiff", "pdiff"));
            MATERIAL_NDIFF = PLANE_DIFF.getMaterials().get(0);
            MATERIAL_PDIFF = PLANE_DIFF.getMaterials().get(1);
            PLANE_POLY = new PlaneSchema("poly");
            MATERIAL_POLY = PLANE_POLY.getMaterials().get(0);
            PLANE_METAL1 = new PlaneSchema("metal1", ImmutableList.of("contact", "metal1"));
            MATERIAL_CONTACT = PLANE_METAL1.getMaterials().get(0);
            MATERIAL_METAL1 = PLANE_METAL1.getMaterials().get(1);
            PLANE_METAL2 = new PlaneSchema("metal2", ImmutableList.of("via12", "metal2"));
            MATERIAL_VIA12 = PLANE_METAL2.getMaterials().get(0);
            MATERIAL_METAL2 = PLANE_METAL2.getMaterials().get(1);
            PLANE_PAD = new PlaneSchema("pad");
            MATERIAL_PAD = PLANE_PAD.getMaterials().get(0);
            TECHNOLOGY = new Technology("concept", ImmutableList.of(PLANE_WELL, PLANE_DIFF, PLANE_POLY, PLANE_METAL1, PLANE_METAL2, PLANE_PAD));
        }

    }

    /**
     * This closely resembles the LibreSilicon designs in Magic.
     */
    public static final class LibreSiliconMagicScmos {

        public static final Technology TECHNOLOGY;

        public static final PlaneSchema PLANE_WELL = 0;
        public static final PlaneSchema PLANE_ACTIVE = 1;
        public static final PlaneSchema PLANE_METAL1 = 2;
        public static final PlaneSchema PLANE_METAL2 = 3;

        public static final Material MATERIAL_NWELL = 0;
        public static final Material MATERIAL_PWELL = 1;
        public static final Material MATERIAL_POLYSILICON = 0;
        public static final Material MATERIAL_NDIFFUSION = 1;
        public static final Material MATERIAL_PDIFFUSION = 2;
        public static final Material MATERIAL_NTRANSISTOR = 3;
        public static final Material MATERIAL_PTRANSISTOR = 4;
        public static final Material MATERIAL_POLYCONTACT = 5;
        public static final Material MATERIAL_NDCONTACT = 6;
        public static final Material MATERIAL_PDCONTACT = 7;
        public static final Material MATERIAL_NSUBSTRATENCONTACT = 8;
        public static final Material MATERIAL_PSUBSTRATEPCONTACT = 9;
        public static final Material MATERIAL_METAL1 = 0;
        public static final Material MATERIAL_M2CONTACT = 1;
        public static final Material MATERIAL_METAL2 = 0;
        public static final Material MATERIAL_PAD = 1;

        private LibreSiliconMagicScmos() {
        }

        private static Technology build() {
            PlaneSchema well = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
            PlaneSchema active = new PlaneSchema("active", ImmutableList.of(
                    "polysilicon",
                    "ndiffusion", "pdiffusion",
                    "ntransistor", "ptransistor",
                    "polycontact",
                    "ndcontact", "pdcontact",
                    "nsubstratencontact", "psubstratepcontact"
            ));
            PlaneSchema metal1 = new PlaneSchema("metal1", ImmutableList.of("metal1", "m2contact"));
            PlaneSchema metal2 = new PlaneSchema("metal2", ImmutableList.of("metal2", "pad"));
            ImmutableList<PlaneSchema> planeSchemas = ImmutableList.of(well, active, metal1, metal2);
            return new Technology("libresilicon-magic-scmos", planeSchemas);
        }

    }

    private Technologies() {
    }

}
