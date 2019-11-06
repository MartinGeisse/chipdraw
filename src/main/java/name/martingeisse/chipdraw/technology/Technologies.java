package name.martingeisse.chipdraw.technology;

import com.google.common.collect.ImmutableList;

public final class Technologies {

    public static final Technology CONCEPT = buildConceptTechnology();
    public static final Technology LIBRESILICON_MAGIC_SCMOS = buildLibresiliconMagicScmos();

    private static Technology buildConceptTechnology() {
        PlaneSchema well = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
        PlaneSchema diff = new PlaneSchema("diff", ImmutableList.of("ndiff", "pdiff"));
        PlaneSchema poly = new PlaneSchema("poly");
        PlaneSchema contact = new PlaneSchema("contact");
        PlaneSchema metal1 = new PlaneSchema("metal1");
        ImmutableList<PlaneSchema> planeSchemas = ImmutableList.of(well, diff, poly, contact, metal1);
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
                "nsubstratencontact", "psubstratepcontact",
                "nsubstratendiff", "psubstratepdiff"
        ));
        PlaneSchema metal1 = new PlaneSchema("metal1", ImmutableList.of("metal1", "m2contact"));
        PlaneSchema metal2 = new PlaneSchema("metal2", ImmutableList.of("metal2", "pad"));
        ImmutableList<PlaneSchema> planeSchemas = ImmutableList.of(well, active, metal1, metal2);
        return new Technology("libresilicon-magic-scmos", planeSchemas);
    }

    private Technologies() {
    }

}
