package name.martingeisse.chipdraw.drc.rule;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;

public abstract class AbstractRule implements Rule {

    private String errorMessage;

    public final Rule setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public final String getErrorMessage() {
        return errorMessage;
    }

    protected String getEffectiveErrorMessage() {
        return errorMessage != null ? errorMessage : getClass().getSimpleName();
    }

    protected final boolean hasMinimumWidthWithAnyMaterial(Plane plane, int x, int y, int expectedWidth) {
        for (int dx = -expectedWidth + 1; dx <= 0; dx++) {
            for (int dy = -expectedWidth + 1; dy <= 0; dy++) {
                if (!plane.isRectangleContainsMaterialAutoclip(x + dx, y + dy, expectedWidth, expectedWidth, Material.NONE)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected final boolean hasMinimumWidthWithMaterial(Plane plane, int x, int y, int expectedWidth, Material material) {
        for (int dx = -expectedWidth + 1; dx <= 0; dx++) {
            for (int dy = -expectedWidth + 1; dy <= 0; dy++) {
                if (plane.isRectangleUniformAutoclip(x + dx, y + dy, expectedWidth, expectedWidth, material)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected final boolean isSurroundedByMaterial(Plane plane, int x, int y, int distance, Material material) {
        int size = 2 * distance + 1;
        return plane.isRectangleUniformAutoclip(x - distance, y - distance, size, size, material);
    }

    protected final boolean isMaterialNearby(Plane plane, int x, int y, int distance, Material material) {
        int size = 2 * distance + 1;
        return plane.isRectangleContainsMaterialAutoclip(x - distance, y - distance, size, size, material);
    }

    protected final boolean isSurroundedByAnyMaterial(Plane plane, int x, int y, int distance) {
        return !isMaterialNearby(plane, x, y, distance, Material.NONE);
    }

    protected final boolean isAnyMaterialNearby(Plane plane, int x, int y, int distance) {
        return !isSurroundedByMaterial(plane, x, y, distance, Material.NONE);
    }

    protected final boolean hasMinimumExtensionWithMaterial(Plane plane, int x, int y, int distance, Material material) {
        if (material == null) {
            throw new IllegalArgumentException("material cannot be null");
        }
        return hasMinimumExtensionInternal(plane, x, y, distance, material);
    }


    protected final boolean hasMinimumExtensionWithAnyMaterial(Plane plane, int x, int y, int distance) {
        return hasMinimumExtensionInternal(plane, x, y, distance, null);
    }

    private boolean hasMinimumExtensionInternal(Plane plane, int x, int y, int distance, Material material) {
        boolean xOkay = true, yOkay = true;
        for (int delta = -distance; delta <= distance; delta++) {
            if (material == null ? (plane.getPixelAutoclip(x + delta, y) == Material.NONE) : (plane.getPixelAutoclip(x + delta, y) != material)) {
                xOkay = false;
            }
            if (material == null ? (plane.getPixelAutoclip(delta, y + delta) == Material.NONE) : (plane.getPixelAutoclip(x, y + delta) != material)) {
                yOkay = false;
            }
        }
        return xOkay || yOkay;
    }

}
