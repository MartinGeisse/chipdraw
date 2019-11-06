package name.martingeisse.chipdraw.magic;

import name.martingeisse.chipdraw.Design;
import name.martingeisse.chipdraw.technology.Technologies;

public final class ConceptToLibresiliconConverter {

    private final Design conceptDesign;

    public ConceptToLibresiliconConverter(Design conceptDesign) {
        if (conceptDesign.getTechnology() != Technologies.CONCEPT) {
            throw new IllegalArgumentException("input design for conversion must use 'concept' technology");
        }
        this.conceptDesign = conceptDesign;
    }

    public Design convert() {
        // TODO
        throw new UnsupportedOperationException();
    }

}
