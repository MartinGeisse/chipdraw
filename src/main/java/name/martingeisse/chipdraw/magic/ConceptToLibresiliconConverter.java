package name.martingeisse.chipdraw.magic;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.Design;
import name.martingeisse.chipdraw.Plane;
import name.martingeisse.chipdraw.technology.Technologies;

public final class ConceptToLibresiliconConverter {

    private final Design original;

    public ConceptToLibresiliconConverter(Design conceptDesign) {
        if (conceptDesign.getTechnology() != Technologies.CONCEPT) {
            throw new IllegalArgumentException("input design for conversion must use 'concept' technology");
        }
        this.original = conceptDesign;
    }

    public Design convert() {
        Design converted = new Design(Technologies.LIBRESILICON_MAGIC_SCMOS, original.getWidth(), original.getHeight());
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {

                // read original pixels
                int originalWell = original.getPlanes().get(0).getCell(x, y);
                int originalDiff = original.getPlanes().get(1).getCell(x, y);
                int originalPoly = original.getPlanes().get(2).getCell(x, y);
                int originalMetal1 = original.getPlanes().get(3).getCell(x, y);

                // wells can be copied directly
                converted.getPlanes().get(0).setCell(x, y, originalWell);

                // TODO active
                // converted.getPlanes().get(1).setCell(x, y, );





            }
        }



        // TODO
        throw new UnsupportedOperationException();
    }

}
