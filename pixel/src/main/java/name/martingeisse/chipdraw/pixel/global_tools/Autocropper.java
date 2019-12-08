package name.martingeisse.chipdraw.pixel.global_tools;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

import java.util.function.Predicate;

/**
 * Reduces a design to the smallest rectangle needed to contain all its nonempty pixels. Returns the result as a
 * new design object.
 */
public final class Autocropper {

    private final Design design;

    public Autocropper(Design design) {
        this.design = design;
    }

    public Design autocrop() throws UserVisibleMessageException {
        if (isDesignEmpty()) {
            throw new UserVisibleMessageException("design is empty");
        }
        int top = getFirstFalse(this::isRowEmpty);
        int bottom = getFirstFalse(n -> isRowEmpty(design.getHeight() - 1 - n));
        int left = getFirstFalse(this::isColumnEmpty);
        int right = getFirstFalse(n -> isColumnEmpty(design.getWidth() - 1 - n));
        return design.createCopyOfRectangle(left, top, design.getWidth() - left - right, design.getHeight() - top - bottom);
    }

    private boolean isDesignEmpty() {
        for (Plane plane : design.getPlanes()) {
            if (!plane.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private int getFirstFalse(Predicate<Integer> tester) {
        int i = 0;
        while (tester.test(i)) {
            i++;
        }
        return i;
    }

    private boolean isRowEmpty(int y) {
        for (Plane plane : design.getPlanes()) {
            if (!plane.isRectangleUniform(0, y, design.getWidth(), 1, Material.NONE)) {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnEmpty(int x) {
        for (Plane plane : design.getPlanes()) {
            if (!plane.isRectangleUniform(x, 0, 1, design.getHeight(), Material.NONE)) {
                return false;
            }
        }
        return true;
    }

}
