package name.martingeisse.chipdraw.technology;

import com.google.common.collect.ImmutableList;

public final class Technologies {

    public static final Technology CONCEPT = buildConceptTechnology();

    private static Technology buildConceptTechnology() {
        PlaneSchema well = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
        PlaneSchema diff = new PlaneSchema("diff", ImmutableList.of("ndiff", "pdiff"));
        PlaneSchema poly = new PlaneSchema("poly");
        PlaneSchema contact = new PlaneSchema("contact");
        PlaneSchema metal1 = new PlaneSchema("metal1");
        ImmutableList<PlaneSchema> planeSchemas = ImmutableList.of(well, diff, poly, contact, metal1);
        return new Technology("concept", planeSchemas);
    }

    private Technologies() {
    }

}
