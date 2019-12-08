package name.martingeisse.chipdraw.pixel.global_tools.stdcell;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.design.Technologies;

/**
 * This base class can be used to generate standard cell templates but will use generic values that might be off for a
 * specific process. Define a subclass to set more specific values.
 */
public class StandardCellTemplateGeneratorBase {

//region fields

    // general
    private final int width = 140;
    private final int height = 70;

    // wells
    private final int topWellMargin = 1;
    private final int bottomWellMargin = 1;
    private final int leftNwellMargin = 2;
    private final int rightNwellMargin = 2;
    private final int leftPwellMargin = 2;
    private final int rightPwellMargin = 2;
    private final int wellGap = 2;

    // power rails
    private final int powerRailWidthInTiles = 11;
    private final int powerRailTopMargin = 1;
    private final int powerRailBottomMargin = 2;

    // well taps
    private final int wellTapLeftMargin = 5;
    private final int wellTapSize = 2;
    private final int wellTapSpacing = 3;

//endregion

//region accessors

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTopWellMargin() {
        return topWellMargin;
    }

    public int getBottomWellMargin() {
        return bottomWellMargin;
    }

    public int getLeftNwellMargin() {
        return leftNwellMargin;
    }

    public int getRightNwellMargin() {
        return rightNwellMargin;
    }

    public int getLeftPwellMargin() {
        return leftPwellMargin;
    }

    public int getRightPwellMargin() {
        return rightPwellMargin;
    }

    public int getWellGap() {
        return wellGap;
    }

    public int getPowerRailWidthInTiles() {
        return powerRailWidthInTiles;
    }

    public int getPowerRailTopMargin() {
        return powerRailTopMargin;
    }

    public int getPowerRailBottomMargin() {
        return powerRailBottomMargin;
    }

    public int getWellTapLeftMargin() {
        return wellTapLeftMargin;
    }

    public int getWellTapSize() {
        return wellTapSize;
    }

    public int getWellTapSpacing() {
        return wellTapSpacing;
    }

//endregion

    public Design generate() {
        Design design = new Design(Technologies.Concept.TECHNOLOGY, width, height);
        Plane wellPlane = design.getPlane(Technologies.Concept.PLANE_WELL);
        Plane diffusionPlane = design.getPlane(Technologies.Concept.PLANE_DIFF);
        Plane metalPlane = design.getPlane(Technologies.Concept.PLANE_METAL1);

        // wells
        int nwellWidth = width - leftNwellMargin - rightNwellMargin;
        int pwellWidth = width - leftPwellMargin - rightPwellMargin;
        int combinedWellHeight = height - topWellMargin - bottomWellMargin - wellGap;
        int pwellHeight = combinedWellHeight / 3;
        int nwellHeight = combinedWellHeight - pwellHeight;
        wellPlane.drawRectangleAutoclip(leftNwellMargin, topWellMargin, nwellWidth, nwellHeight, Technologies.Concept.MATERIAL_NWELL);
        wellPlane.drawRectangleAutoclip(leftPwellMargin, height - bottomWellMargin - pwellHeight, pwellWidth, pwellHeight, Technologies.Concept.MATERIAL_PWELL);

        // p-well (lower well)
        // TODO

        // power rails
        // TODO

        // well taps
        // TODO

        return design;
    }

}
