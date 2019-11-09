package name.martingeisse.chipdraw.operation;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

/**
 * This class implements undo/redo support manually by modifying the design in-place back and forth.
 */
public abstract class InPlaceDesignOperation extends DesignOperation {

    @Override
    final Design performInternal(Design design) throws UserVisibleMessageException {
        doPerform(design);
        return design;
    }

    @Override
    final Design undoInternal(Design design) throws UserVisibleMessageException {
        doUndo(design);
        return design;
    }

    /**
     * Performs this operation by modifying the specified design.
     */
    protected abstract void doPerform(Design design) throws UserVisibleMessageException;

    /**
     * Performs undo of this operation by modifying the specified design back to its original state.
     */
    protected abstract void doUndo(Design design) throws UserVisibleMessageException;

}
