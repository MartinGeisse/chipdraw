package name.martingeisse.chipdraw.pixel.scmos.magic;

import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;

public final class ConceptToMagicConverter {

    private final Design original;

    public ConceptToMagicConverter(Design conceptDesign) {
        ConceptSchemas.validateConforms(conceptDesign.getTechnology());
        this.original = conceptDesign;
    }

    public Design convert() throws IncompatibilityException {
        Design converted = new Design(ScmosMagic.TECHNOLOGY, original.getWidth(), original.getHeight());

        // original planes
        Plane originalWellPlane = original.getPlane(ConceptSchemas.PLANE_WELL);
        Plane originalDiffPlane = original.getPlane(ConceptSchemas.PLANE_DIFF);
        Plane originalPolyPlane = original.getPlane(ConceptSchemas.PLANE_POLY);
        Plane originalMetal1Plane = original.getPlane(ConceptSchemas.PLANE_METAL1);
        Plane originalMetal2Plane = original.getPlane(ConceptSchemas.PLANE_METAL2);
        Plane originalPadPlane = original.getPlane(ConceptSchemas.PLANE_PAD);

        // converted planes
        Plane convertedWellPlane = converted.getPlane(ScmosMagic.PLANE_WELL);
        Plane convertedActivePlane = converted.getPlane(ScmosMagic.PLANE_ACTIVE);
        Plane convertedMetal1Plane = converted.getPlane(ScmosMagic.PLANE_METAL1);
        Plane convertedMetal2Plane = converted.getPlane(ScmosMagic.PLANE_METAL2);

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
                if (originalWell == ConceptSchemas.MATERIAL_NWELL) {
                    convertedWellPlane.setPixel(x, y, ScmosMagic.MATERIAL_NWELL);
                } else if (originalWell == ConceptSchemas.MATERIAL_PWELL) {
                    convertedWellPlane.setPixel(x, y, ScmosMagic.MATERIAL_PWELL);
                }

                // active has lots of different upwards contact types in Magic, all of which are represented by
                // downwards contacts in metal1 in our "concept" tech
                if (originalMetal1 == ConceptSchemas.MATERIAL_CONTACT) {
                    // concept metal1 has a downwards contact

                    if (originalPoly != Material.NONE) {
                        convertedActivePlane.setPixel(x, y, ScmosMagic.MATERIAL_POLYCONTACT);
                    } else if (originalDiff != Material.NONE) {
                        convertedActivePlane.setPixel(x, y,
                            originalDiff == ConceptSchemas.MATERIAL_NDIFF ?
                                    ScmosMagic.MATERIAL_NDCONTACT :
                                    ScmosMagic.MATERIAL_PDCONTACT);
                    } else if (originalWell != Material.NONE) {
                        convertedActivePlane.setPixel(x, y,
                            originalWell == ConceptSchemas.MATERIAL_NWELL ?
                                    ScmosMagic.MATERIAL_NSUBSTRATENCONTACT :
                                    ScmosMagic.MATERIAL_PSUBSTRATEPCONTACT);
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
                                    originalDiff == ConceptSchemas.MATERIAL_NDIFF ?
                                            ScmosMagic.MATERIAL_NTRANSISTOR :
                                            ScmosMagic.MATERIAL_PTRANSISTOR);
                        } else {
                            convertedActivePlane.setPixel(x, y, ScmosMagic.MATERIAL_POLYSILICON);
                        }
                    } else if (originalDiff != Material.NONE) {
                        convertedActivePlane.setPixel(x, y,
                                originalDiff == ConceptSchemas.MATERIAL_NDIFF ?
                                        ScmosMagic.MATERIAL_NDIFFUSION :
                                        ScmosMagic.MATERIAL_PDIFFUSION);
                    }

                }

                // magic/SCMOS metal1 plane contains metal1/2 vias as "m2contact", which are in the metal2 plane in the "concept" tech
                if (originalMetal1 == Material.NONE) {
                    if (originalMetal2 == ConceptSchemas.MATERIAL_VIA12) {
                        throw new IncompatibilityException(x, y, "via12 without metal1");
                    } // else: empty
                } else {
                    if (originalMetal2 == ConceptSchemas.MATERIAL_VIA12) {
                        convertedMetal1Plane.setPixel(x, y, ScmosMagic.MATERIAL_M2CONTACT);
                    } else {
                        convertedMetal1Plane.setPixel(x, y, ScmosMagic.MATERIAL_METAL1);
                    }
                }

                // magic/SCMOS metal2 plane contains pads as "pad", which are in the "pad" plane in the "concept" tech
                if (originalMetal2 == Material.NONE) {
                    if (originalPad == ConceptSchemas.MATERIAL_PAD) {
                        throw new IncompatibilityException(x, y, "pad without metal2");
                    } // else: empty
                } else {
                    if (originalPad == ConceptSchemas.MATERIAL_PAD) {
                        convertedMetal2Plane.setPixel(x, y, ScmosMagic.MATERIAL_PAD);
                    } else {
                        convertedMetal2Plane.setPixel(x, y, ScmosMagic.MATERIAL_METAL2);
                    }
                }

            }
        }
        return converted;
    }

    public static class IncompatibilityException extends Exception {

        public IncompatibilityException(String message) {
            super("design cannot be expressed as Magic/SCMOS: " + message);
        }

        public IncompatibilityException(int x, int y, String message) {
            super("pixel at " + x + ", " + y + " cannot be expressed as Magic/SCMOS: " + message);
        }

    }

}
