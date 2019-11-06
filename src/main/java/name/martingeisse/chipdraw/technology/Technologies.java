package name.martingeisse.chipdraw.technology;

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
    public static final Technology CONCEPT = buildConceptTechnology();

    /**
     * This closely resembles the LibreSilicon designs in Magic.
     */
    public static final Technology LIBRESILICON_MAGIC_SCMOS = buildLibresiliconMagicScmos();

    private static Technology buildConceptTechnology() {
        PlaneSchema well = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
        PlaneSchema diff = new PlaneSchema("diff", ImmutableList.of("ndiff", "pdiff"));
        PlaneSchema poly = new PlaneSchema("poly");
        PlaneSchema metal1 = new PlaneSchema("metal1", ImmutableList.of("contact", "metal1"));
        PlaneSchema metal2 = new PlaneSchema("metal2", ImmutableList.of("via12", "metal2"));
        ImmutableList<PlaneSchema> planeSchemas = ImmutableList.of(well, diff, poly, metal1, metal2);
        return new Technology("concept", planeSchemas);
    }

    private static Technology buildLibresiliconMagicScmos() {
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

    private Technologies() {
    }

}
