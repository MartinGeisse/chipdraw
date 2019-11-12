package name.martingeisse.chipdraw.global_tools;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

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
        Design result = new Design(design.getTechnology(), design.getWidth() - left - right, design.getHeight() - top - bottom);
        result.copyFrom(design, left, top, 0, 0, result.getWidth(), result.getHeight());
        return result;
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
            if (!plane.isReactangleUniform(0, y, design.getWidth(), 1, Material.NONE)) {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnEmpty(int x) {
        for (Plane plane : design.getPlanes()) {
            if (!plane.isReactangleUniform(x, 0, 1, design.getHeight(), Material.NONE)) {
                return false;
            }
        }
        return true;
    }

}
