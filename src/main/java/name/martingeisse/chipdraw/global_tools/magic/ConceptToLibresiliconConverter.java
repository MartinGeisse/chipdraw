package name.martingeisse.chipdraw.global_tools.magic;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.Technologies;

public final class ConceptToLibresiliconConverter {

    private final Design original;

    public ConceptToLibresiliconConverter(Design conceptDesign) {
        if (conceptDesign.getTechnology() != Technologies.Concept.TECHNOLOGY) {
            throw new IllegalArgumentException("input design for conversion must use 'concept' technology");
        }
        this.original = conceptDesign;
    }

    public Design convert() throws IncompatibilityException {
        Design converted = new Design(Technologies.LibreSiliconMagicScmos.TECHNOLOGY, original.getWidth(), original.getHeight());

        // original planes
        Plane originalWellPlane = original.getPlane(Technologies.Concept.PLANE_WELL);
        Plane originalDiffPlane = original.getPlane(Technologies.Concept.PLANE_DIFF);
        Plane originalPolyPlane = original.getPlane(Technologies.Concept.PLANE_POLY);
        Plane originalMetal1Plane = original.getPlane(Technologies.Concept.PLANE_METAL1);
        Plane originalMetal2Plane = original.getPlane(Technologies.Concept.PLANE_METAL2);
        Plane originalPadPlane = original.getPlane(Technologies.Concept.PLANE_PAD);

        // converted planes
        Plane convertedWellPlane = converted.getPlane(Technologies.LibreSiliconMagicScmos.PLANE_WELL);
        Plane convertedActivePlane = converted.getPlane(Technologies.LibreSiliconMagicScmos.PLANE_ACTIVE);
        Plane convertedMetal1Plane = converted.getPlane(Technologies.LibreSiliconMagicScmos.PLANE_METAL1);
        Plane convertedMetal2Plane = converted.getPlane(Technologies.LibreSiliconMagicScmos.PLANE_METAL2);

        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {

                // read original pixels
                Material originalWell = originalWellPlane.getPixel(x, y);
                Material originalDiff = originalDiffPlane.getPixel(x, y);
                Material originalPoly = originalPolyPlane.getPixel(x, y);
                Material originalMetal1 = originalMetal1Plane.getPixel(x, y);
                Material originalMetal2 = originalMetal2Plane.getPixel(x, y);
                Material originalPad = originalPadPlane.getPixel(x, y);

                // wells can be copied directly
                convertedWellPlane.setPixel(x, y, originalWell == Technologies.Concept.MATERIAL_NWELL ?
                        Technologies.LibreSiliconMagicScmos.MATERIAL_NWELL : Technologies.LibreSiliconMagicScmos.MATERIAL_PWELL);

                // active has lots of different upwards contact types in Magic, all of which are represented by
                // downwards contacts in metal1 in our "concept" tech
                if (originalMetal1 == Technologies.Concept.MATERIAL_CONTACT) {
                    // concept metal1 has a downwards contact

                    if (originalPoly != Material.NONE) {
                        convertedActivePlane.setPixel(x, y, Technologies.LibreSiliconMagicScmos.MATERIAL_POLYCONTACT);
                    } else if (originalDiff != Material.NONE) {
                        convertedActivePlane.setPixel(x, y,
                            originalDiff == Technologies.Concept.MATERIAL_NDIFF ?
                                Technologies.LibreSiliconMagicScmos.MATERIAL_NDCONTACT :
                                    Technologies.LibreSiliconMagicScmos.MATERIAL_PDCONTACT);
                    } else if (originalWell != Material.NONE) {
                        convertedActivePlane.setPixel(x, y,
                            originalWell == Technologies.Concept.MATERIAL_NWELL ?
                                Technologies.LibreSiliconMagicScmos.MATERIAL_NSUBSTRATENCONTACT :
                                    Technologies.LibreSiliconMagicScmos.MATERIAL_PSUBSTRATEPCONTACT);
                    } else {
                        // TODO we must decide for either a n-substrate or twin-well process. The reduced information
                        // in the "concept" technology is otherwise not enough.
                        throw new IncompatibilityException(x, y, "contact without poly, diff or well");
                    }

                } else {
                    // no downwards contact

                    if (originalPoly != Material.NONE) {
                        if (originalDiff != Material.NONE) {
                            convertedActivePlane.setPixel(x, y,
                                    originalDiff == Technologies.Concept.MATERIAL_NDIFF ?
                                            Technologies.LibreSiliconMagicScmos.MATERIAL_NTRANSISTOR :
                                            Technologies.LibreSiliconMagicScmos.MATERIAL_PTRANSISTOR);
                        } else {
                            convertedActivePlane.setPixel(x, y, Technologies.LibreSiliconMagicScmos.MATERIAL_POLYSILICON);
                        }
                    } else if (originalDiff != Material.NONE) {
                        convertedActivePlane.setPixel(x, y,
                                originalDiff == Technologies.Concept.MATERIAL_NDIFF ?
                                        Technologies.LibreSiliconMagicScmos.MATERIAL_NDIFFUSION :
                                        Technologies.LibreSiliconMagicScmos.MATERIAL_PDIFFUSION);
                    }

                }

                // LibreSilicon metal1 plane contains metal1/2 vias as "m2contact", which are in the metal2 plane in the "concept" tech
                if (originalMetal1 == Material.NONE) {
                    if (originalMetal2 == Technologies.Concept.MATERIAL_VIA12) {
                        throw new IncompatibilityException(x, y, "via12 without metal1");
                    } // else: empty
                } else {
                    if (originalMetal2 == Technologies.Concept.MATERIAL_VIA12) {
                        convertedMetal1Plane.setPixel(x, y, Technologies.LibreSiliconMagicScmos.MATERIAL_M2CONTACT);
                    } else {
                        convertedMetal1Plane.setPixel(x, y, Technologies.LibreSiliconMagicScmos.MATERIAL_METAL1);
                    }
                }

                // LibreSilicon metal2 plane contains pads as "pad", which are in the "pad" plane in the "concept" tech
                if (originalMetal2 == Material.NONE) {
                    if (originalPad == Technologies.Concept.MATERIAL_PAD) {
                        throw new IncompatibilityException(x, y, "pad without metal2");
                    } // else: empty
                } else {
                    if (originalPad == Technologies.Concept.MATERIAL_PAD) {
                        convertedMetal2Plane.setPixel(x, y, Technologies.LibreSiliconMagicScmos.MATERIAL_PAD);
                    } else {
                        convertedMetal2Plane.setPixel(x, y, Technologies.LibreSiliconMagicScmos.MATERIAL_METAL2);
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
