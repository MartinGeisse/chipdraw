package name.martingeisse.chipdraw.pixel.operation.scmos;

import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Material;

public enum ScmosContactType {

    NDIFF(ConceptSchemas.MATERIAL_NDIFF),
    PDIFF(ConceptSchemas.MATERIAL_PDIFF),
    POLY(ConceptSchemas.MATERIAL_POLY);

    private final Material lowerMaterial;

    ScmosContactType(Material lowerMaterial) {
        this.lowerMaterial = lowerMaterial;
    }

    public Material getLowerMaterial() {
        return lowerMaterial;
    }

}
