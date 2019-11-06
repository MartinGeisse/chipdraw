package name.martingeisse.chipdraw.technology;

import com.google.common.collect.ImmutableList;

public final class Technologies {

    public static final class Concept {

        public static final int PLANE_WELL = 0;
        public static final int PLANE_DIFF = 1;
        public static final int PLANE_POLY = 2;
        public static final int PLANE_METAL1 = 3;
        public static final int PLANE_METAL2 = 4;
        public static final int PLANE_PAD = 5;

        public static final int MATERIAL_LOCAL_WELL_NWELL = 0;
        public static final int MATERIAL_LOCAL_WELL_PWELL = 1;
        public static final int MATERIAL_LOCAL_DIFF_NDIFF = 0;
        public static final int MATERIAL_LOCAL_DIFF_PDIFF = 1;
        public static final int MATERIAL_LOCAL_POLY_POLY = 0;
        public static final int MATERIAL_LOCAL_METAL1_CONTACT = 0;
        public static final int MATERIAL_LOCAL_METAL1_METAL1 = 1;
        public static final int MATERIAL_LOCAL_METAL2_VIA12 = 0;
        public static final int MATERIAL_LOCAL_METAL2_METAL2 = 1;
        public static final int MATERIAL_LOCAL_PAD_PAD = 0;

        private Concept() {
        }

        private static Technology build() {
            PlaneSchema well = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
            PlaneSchema diff = new PlaneSchema("diff", ImmutableList.of("ndiff", "pdiff"));
            PlaneSchema poly = new PlaneSchema("poly");
            PlaneSchema metal1 = new PlaneSchema("metal1", ImmutableList.of("contact", "metal1"));
            PlaneSchema metal2 = new PlaneSchema("metal2", ImmutableList.of("via12", "metal2"));
            PlaneSchema pad = new PlaneSchema("pad");
            ImmutableList<PlaneSchema> planeSchemas = ImmutableList.of(well, diff, poly, metal1, metal2, pad);
            return new Technology("concept", planeSchemas);
        }

    }

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
    public static final Technology CONCEPT = Concept.build();

    public static final class LibreSiliconMagicScmos {

        public static final int PLANE_WELL = 0;
        public static final int PLANE_ACTIVE = 1;
        public static final int PLANE_METAL1 = 2;
        public static final int PLANE_METAL2 = 3;

        public static final int MATERIAL_LOCAL_WELL_NWELL = 0;
        public static final int MATERIAL_LOCAL_WELL_PWELL = 1;
        public static final int MATERIAL_LOCAL_ACTIVE_POLYSILICON = 0;
        public static final int MATERIAL_LOCAL_ACTIVE_NDIFFUSION = 1;
        public static final int MATERIAL_LOCAL_ACTIVE_PDIFFUSION = 2;
        public static final int MATERIAL_LOCAL_ACTIVE_NTRANSISTOR = 3;
        public static final int MATERIAL_LOCAL_ACTIVE_PTRANSISTOR = 4;
        public static final int MATERIAL_LOCAL_ACTIVE_POLYCONTACT = 5;
        public static final int MATERIAL_LOCAL_ACTIVE_NDCONTACT = 6;
        public static final int MATERIAL_LOCAL_ACTIVE_PDCONTACT = 7;
        public static final int MATERIAL_LOCAL_ACTIVE_NSUBSTRATENCONTACT = 8;
        public static final int MATERIAL_LOCAL_ACTIVE_PSUBSTRATEPCONTACT = 9;
        public static final int MATERIAL_LOCAL_METAL1_METAL1 = 0;
        public static final int MATERIAL_LOCAL_METAL1_M2CONTACT = 1;
        public static final int MATERIAL_LOCAL_METAL2_METAL2 = 0;
        public static final int MATERIAL_LOCAL_METAL2_PAD = 1;

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

    /**
     * This closely resembles the LibreSilicon designs in Magic.
     */
    public static final Technology LIBRESILICON_MAGIC_SCMOS = LibreSiliconMagicScmos.build();

    private Technologies() {
    }

}
