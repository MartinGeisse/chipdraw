package name.martingeisse.chipdraw.drc.rule.experiment;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.drc.rule.Rule;

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
        // TODO autoclip -> use isUniform
        for (int dx = -distance; dx <= distance; dx++) {
            for (int dy = -distance; dy <= distance; dy++) {
                if (plane.getPixel(x + dx, y + dy) != material) {
                    return false;
                }
            }
        }
        return true;
    }

    protected final boolean isMaterialNearby(Plane plane, int x, int y, int distance, Material material) {
        // TODO autoclip -> use isUniform
        for (int dx = -distance; dx <= distance; dx++) {
            for (int dy = -distance; dy <= distance; dy++) {
                if (plane.getPixel(x + dx, y + dy) == material) {
                    return true;
                }
            }
        }
        return false;
    }

    protected final boolean isSurroundedByAnyMaterial(Plane plane, int x, int y, int distance) {
        return !isMaterialNearby(plane, x, y, distance, Material.NONE);
    }

    protected final boolean isAnyMaterialNearby(Plane plane, int x, int y, int distance) {
        return !isSurroundedByMaterial(plane, x, y, distance, Material.NONE);
    }

}
