package name.martingeisse.chipdraw.pixel.operation;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

/**
 * Implementations cannot be re-used to perform an operation multiple times -- the state of this object is stored
 * on the undo stack.
 * <p>
 * This class cannot be used directly. Use one of the subclasses to implement an operation.
 */
public abstract class DesignOperation {

    DesignOperation() {
    }

    abstract Design performInternal(Design design) throws UserVisibleMessageException;

    final Design performInternalNullChecked(Design design) throws UserVisibleMessageException {
        if (design == null) {
            throw new IllegalArgumentException("design argument is null");
        }
        Design newDesign = performInternal(design);
        if (newDesign == null) {
            throw new RuntimeException("operation returned null");
        }
        return newDesign;
    }

    abstract Design undoInternal(Design design) throws UserVisibleMessageException;

    final Design undoInternalNullChecked(Design design) throws UserVisibleMessageException {
        if (design == null) {
            throw new IllegalArgumentException("design argument is null");
        }
        Design newDesign = undoInternal(design);
        if (newDesign == null) {
            throw new RuntimeException("operation returned null");
        }
        return newDesign;
    }

}
