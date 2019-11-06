package name.martingeisse.chipdraw.magic;

import name.martingeisse.chipdraw.Design;
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

                // wells can be copied directly
                converted.getPlanes().get(0).setCell(x, y, original.getPlanes().get(0).getCell(x, y));



            }
        }



        // TODO
        throw new UnsupportedOperationException();
    }

}
