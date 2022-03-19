package name.martingeisse.chipdraw.pixel.scmos;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.PlaneSchema;
import name.martingeisse.chipdraw.pixel.design.Technology;
import name.martingeisse.chipdraw.pixel.design.TechnologyBehavior;
import name.martingeisse.chipdraw.pixel.drc.Drc;
import name.martingeisse.chipdraw.pixel.scmos.drc.ScmosConceptDrc;

public final class ScmosConcept {

    public static final Technology TECHNOLOGY = new Technology("ScmosConcept",
            ConceptSchemas.PLANE_LIST, new TechnologyBehavior() {

        @Override
        public ImmutableList<ImmutableList<PlaneSchema>> getPlaneGroups() {
            return ConceptSchemas.PLANE_GROUPS;
        }

        @Override
        public Drc getDrc() {
            return new ScmosConceptDrc();
        }

    });

    private ScmosConcept() {
    }

}
