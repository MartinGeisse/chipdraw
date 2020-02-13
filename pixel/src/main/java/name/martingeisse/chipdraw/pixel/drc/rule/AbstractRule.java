package name.martingeisse.chipdraw.pixel.drc.rule;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;

public abstract class AbstractRule implements Rule {

    private String errorMessageOverride;

    public final Rule setErrorMessageOverride(String errorMessageOverride) {
        if (errorMessageOverride != null) {
            errorMessageOverride = errorMessageOverride.trim();
            if (errorMessageOverride.isEmpty()) {
                throw new IllegalArgumentException("Error message cannot be empty. Pass null to use the default message.");
            }
        }
        this.errorMessageOverride = errorMessageOverride;
        return this;
    }

    public String getErrorMessageOverride() {
        return errorMessageOverride;
    }

    protected final String getErrorMessage() {
        return errorMessageOverride == null ? getImplicitErrorMessage() : errorMessageOverride;
    }

    protected String getImplicitErrorMessage() {
        String message = getClass().getSimpleName();
        if (message != null) {
            message = message.trim();
            if (!message.isEmpty()) {
                return message;
            }
        }
        message = getClass().getName();
        if (message != null) {
            message = message.trim();
            if (!message.isEmpty()) {
                return message;
            }
        }
        return "DRC ERROR";
    }

    protected final boolean hasMinimumWidthWithAnyMaterial(Plane plane, int x, int y, int expectedWidth) {
        for (int dx = -expectedWidth + 1; dx <= 0; dx++) {
            for (int dy = -expectedWidth + 1; dy <= 0; dy++) {
                if (!plane.isRectangleContainsMaterial(x + dx, y + dy, expectedWidth, expectedWidth, Material.NONE)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected final boolean hasMinimumWidthWithMaterial(Plane plane, int x, int y, int expectedWidth, Material material) {
        for (int dx = -expectedWidth + 1; dx <= 0; dx++) {
            for (int dy = -expectedWidth + 1; dy <= 0; dy++) {
                if (plane.isRectangleUniform(x + dx, y + dy, expectedWidth, expectedWidth, material)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected final boolean isSurroundedByMaterial(Plane plane, int x, int y, int distance, Material material) {
        int size = 2 * distance + 1;
        return plane.isRectangleUniform(x - distance, y - distance, size, size, material);
    }

    protected final boolean isMaterialNearby(Plane plane, int x, int y, int distance, Material material) {
        int size = 2 * distance + 1;
        return plane.isRectangleContainsMaterial(x - distance, y - distance, size, size, material);
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
            if (material == null ? (plane.getPixel(x + delta, y) == Material.NONE) : (plane.getPixel(x + delta, y) != material)) {
                xOkay = false;
            }
            if (material == null ? (plane.getPixel(x, y + delta) == Material.NONE) : (plane.getPixel(x, y + delta) != material)) {
                yOkay = false;
            }
        }
        return xOkay || yOkay;
    }

}
