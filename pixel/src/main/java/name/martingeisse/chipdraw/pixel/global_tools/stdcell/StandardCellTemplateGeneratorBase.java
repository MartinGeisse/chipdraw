package name.martingeisse.chipdraw.pixel.global_tools.stdcell;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.design.Technologies;

/**
 * This base class can be used to generate standard cell templates but will use generic values that might be off for a
 * specific process. Define a subclass to set more specific values.
 */
public class StandardCellTemplateGeneratorBase {

//region fields

    // general
    private int width = 280;
    private int height = 70;

    // wells
    private int topWellMargin = 0;
    private int bottomWellMargin = 0;
    private int leftWellMargin = 0;
    private int rightWellMargin = 0;
    private int wellGap = 0;
    private int nwellHeight = 39;

    // power rails
    private int powerRailHeight = 11;
    private int powerRailTopMargin = 1;
    private int powerRailBottomMargin = 2;

    // well taps
    private int wellTapMargin = 5;
    private int wellTapSize = 2;
    private int wellTapSpacing = 2;
    private int overlapByDiffusion = 2;

//endregion

//region accessors

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTopWellMargin() {
        return topWellMargin;
    }

    public void setTopWellMargin(int topWellMargin) {
        this.topWellMargin = topWellMargin;
    }

    public int getBottomWellMargin() {
        return bottomWellMargin;
    }

    public void setBottomWellMargin(int bottomWellMargin) {
        this.bottomWellMargin = bottomWellMargin;
    }

    public int getLeftWellMargin() {
        return leftWellMargin;
    }

    public void setLeftWellMargin(int leftWellMargin) {
        this.leftWellMargin = leftWellMargin;
    }

    public int getRightWellMargin() {
        return rightWellMargin;
    }

    public void setRightWellMargin(int rightWellMargin) {
        this.rightWellMargin = rightWellMargin;
    }

    public int getWellGap() {
        return wellGap;
    }

    public void setWellGap(int wellGap) {
        this.wellGap = wellGap;
    }

    public int getPowerRailHeight() {
        return powerRailHeight;
    }

    public void setPowerRailHeight(int powerRailHeight) {
        this.powerRailHeight = powerRailHeight;
    }

    public int getPowerRailTopMargin() {
        return powerRailTopMargin;
    }

    public void setPowerRailTopMargin(int powerRailTopMargin) {
        this.powerRailTopMargin = powerRailTopMargin;
    }

    public int getPowerRailBottomMargin() {
        return powerRailBottomMargin;
    }

    public void setPowerRailBottomMargin(int powerRailBottomMargin) {
        this.powerRailBottomMargin = powerRailBottomMargin;
    }

    public int getWellTapMargin() {
        return wellTapMargin;
    }

    public void setWellTapMargin(int wellTapMargin) {
        this.wellTapMargin = wellTapMargin;
    }

    public int getWellTapSize() {
        return wellTapSize;
    }

    public void setWellTapSize(int wellTapSize) {
        this.wellTapSize = wellTapSize;
    }

    public int getWellTapSpacing() {
        return wellTapSpacing;
    }

    public void setWellTapSpacing(int wellTapSpacing) {
        this.wellTapSpacing = wellTapSpacing;
    }

    public int getOverlapByDiffusion() {
        return overlapByDiffusion;
    }

    public void setOverlapByDiffusion(int overlapByDiffusion) {
        this.overlapByDiffusion = overlapByDiffusion;
    }

    //endregion

    public Design generate() {
        Design design = new Design(Technologies.Concept.TECHNOLOGY, width, height);
        Plane wellPlane = design.getPlane(Technologies.Concept.PLANE_WELL);
        Plane diffusionPlane = design.getPlane(Technologies.Concept.PLANE_DIFF);
        Plane metalPlane = design.getPlane(Technologies.Concept.PLANE_METAL1);

        // wells
        {
            int wellWidth = width - leftWellMargin - rightWellMargin;
            int combinedWellHeight = height - topWellMargin - bottomWellMargin - wellGap;
            int pwellHeight = combinedWellHeight - nwellHeight;
            wellPlane.drawRectangle(leftWellMargin, topWellMargin, wellWidth, nwellHeight, Technologies.Concept.MATERIAL_NWELL);
            wellPlane.drawRectangle(leftWellMargin, height - bottomWellMargin - pwellHeight, wellWidth, pwellHeight, Technologies.Concept.MATERIAL_PWELL);
        }

        // power rails
        metalPlane.drawRectangle(0, powerRailTopMargin, width, powerRailHeight, Technologies.Concept.MATERIAL_METAL1);
        metalPlane.drawRectangle(0, height - powerRailBottomMargin - powerRailHeight, width, powerRailHeight, Technologies.Concept.MATERIAL_METAL1);

        // well taps
        {

            // diffusion
            int nwellY = topWellMargin + wellTapMargin;
            int pwellY = height - bottomWellMargin - wellTapMargin - wellTapSize;
            drawDiffusionForWellTaps(diffusionPlane, Technologies.Concept.MATERIAL_NDIFF, nwellY);
            drawDiffusionForWellTaps(diffusionPlane, Technologies.Concept.MATERIAL_PDIFF, pwellY);

            // contacts
            int x = leftWellMargin + wellTapMargin;
            while (x <= width - rightWellMargin - wellTapMargin - wellTapSize) {
                metalPlane.drawRectangle(x, nwellY, 2, 2, Technologies.Concept.MATERIAL_CONTACT);
                metalPlane.drawRectangle(x, pwellY, 2, 2, Technologies.Concept.MATERIAL_CONTACT);
                x = x + wellTapSize + wellTapSpacing;
            }

        }

        return design;
    }

    private void drawDiffusionForWellTaps(Plane diffusionPlane, Material material, int tapY) {
        int x = leftWellMargin + wellTapMargin - overlapByDiffusion;
        int diffusionWidth = width - leftWellMargin - rightWellMargin - 2 * (wellTapMargin - overlapByDiffusion);
        int diffusionHeight = 2 * overlapByDiffusion + wellTapSize;
        diffusionPlane.drawRectangle(x, tapY - overlapByDiffusion, diffusionWidth, diffusionHeight, material);
    }

}
