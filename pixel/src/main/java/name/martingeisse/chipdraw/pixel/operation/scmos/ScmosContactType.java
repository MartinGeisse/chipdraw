package name.martingeisse.chipdraw.pixel.operation.scmos;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Technologies;

public enum ScmosContactType {

    NDIFF(Technologies.Concept.MATERIAL_NDIFF),
    PDIFF(Technologies.Concept.MATERIAL_PDIFF),
    POLY(Technologies.Concept.MATERIAL_POLY);

    private final Material lowerMaterial;

    ScmosContactType(Material lowerMaterial) {
        this.lowerMaterial = lowerMaterial;
    }

    public Material getLowerMaterial() {
        return lowerMaterial;
    }

}
