package name.martingeisse.chipdraw.magic;

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

    public Design convert() throws IncompatibilityException {
        Design converted = new Design(Technologies.LIBRESILICON_MAGIC_SCMOS, original.getWidth(), original.getHeight());
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {

                // read original pixels
                int originalWell = original.getPlanes().get(Technologies.Concept.PLANE_WELL).getCell(x, y);
                int originalDiff = original.getPlanes().get(Technologies.Concept.PLANE_DIFF).getCell(x, y);
                int originalPoly = original.getPlanes().get(Technologies.Concept.PLANE_POLY).getCell(x, y);
                int originalMetal1 = original.getPlanes().get(Technologies.Concept.PLANE_METAL1).getCell(x, y);
                int originalMetal2 = original.getPlanes().get(Technologies.Concept.PLANE_METAL2).getCell(x, y);
                int originalPad = original.getPlanes().get(Technologies.Concept.PLANE_PAD).getCell(x, y);

                // wells can be copied directly
                converted.getPlanes().get(Technologies.LibreSiliconMagicScmos.PLANE_WELL).setCell(x, y, originalWell);

                // active has lots of different upwards contact types in Magic, all of which are represented by
                // downwards contacts in metal1 in our "concept" tech
                if (originalMetal1 == Technologies.Concept.MATERIAL_LOCAL_METAL1_CONTACT) {
                    // concept metal1 has a downwards contact

                    if (originalPoly != Plane.EMPTY_CELL) {
                        // TODO converted.getPlanes().get(1).setCell(x, y, 1); // via12
                    }

                    // TODO
                    if (originalDiff != Plane.EMPTY_CELL && originalPoly != Plane.EMPTY_CELL) {
                        throw new IncompatibilityException(x, y, "contact is ambiguous since both diff and poly are filled");
                    }
                } else {
                    // no downwards contact
                }

                // LibreSilicon metal1 plane contains metal1/2 vias as "m2contact", which are in the metal2 plane in the "concept" tech
                if (originalMetal1 == Plane.EMPTY_CELL) {
                    if (originalMetal2 == Technologies.Concept.MATERIAL_LOCAL_METAL2_VIA12) {
                        throw new IncompatibilityException(x, y, "via12 without metal1");
                    } // else: empty
                } else {
                    if (originalMetal2 == Technologies.Concept.MATERIAL_LOCAL_METAL2_VIA12) {
                        converted.getPlanes().get(Technologies.LibreSiliconMagicScmos.PLANE_METAL1).setCell(x, y,
                                Technologies.LibreSiliconMagicScmos.MATERIAL_LOCAL_METAL1_M2CONTACT);
                    } else {
                        converted.getPlanes().get(Technologies.LibreSiliconMagicScmos.PLANE_METAL1).setCell(x, y,
                                Technologies.LibreSiliconMagicScmos.MATERIAL_LOCAL_METAL1_METAL1);
                    }
                }

                // LibreSilicon metal2 plane contains pads as "pad", which are in the "pad" plane in the "concept" tech
                if (originalMetal2 == Plane.EMPTY_CELL) {
                    if (originalPad == Technologies.Concept.MATERIAL_LOCAL_PAD_PAD) {
                        throw new IncompatibilityException(x, y, "pad without metal2");
                    } // else: empty
                } else {
                    if (originalPad == Technologies.Concept.MATERIAL_LOCAL_PAD_PAD) {
                        converted.getPlanes().get(Technologies.LibreSiliconMagicScmos.PLANE_METAL2).setCell(x, y,
                                Technologies.LibreSiliconMagicScmos.MATERIAL_LOCAL_METAL2_PAD);
                    } else {
                        converted.getPlanes().get(Technologies.LibreSiliconMagicScmos.PLANE_METAL2).setCell(x, y,
                                Technologies.LibreSiliconMagicScmos.MATERIAL_LOCAL_METAL2_METAL2);
                    }
                }

            }
        }
        return converted;
    }

    public static class IncompatibilityException extends Exception {

        public IncompatibilityException(String message) {
            super("design cannot be expressed as LibreSilicon SCMOS: " + message);
        }

        public IncompatibilityException(int x, int y, String message) {
            super("pixel at " + x + ", " + y + " cannot be expressed as LibreSilicon SCMOS: " + message);
        }

    }

}
