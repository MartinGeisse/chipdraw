package name.martingeisse.chipdraw.operation;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

/**
 * This class implements undo/redo support manually by modifying the design in-place back and forth.
 * <p>
 * MERGING:
 * <p>
 * Some operations are intended to merge with previously performed operations, so they collapse to a single
 * entry on the undo/redo stack. For example, drawing pixels should treat one "stroke" (drawing multiple pixels without
 * releasing the mouse button) as a single undoable operation. Three steps are necessary to implement merging:
 * - implement {@link #doMerge(Design, DesignOperation)} (read the method contract carefully!)
 * - have {@link #doPerform(Design)} TODO
 * - set the "merging" flag
 *
 * The "merging" flag allows to control from the outside whether merging should be attempted. In the pixel-drawing
 * example, the "merging" flag is set from the outside to prevent merging of the first pixel of a new "stroke" into
 * the previous "stroke".
 */
public abstract class InPlaceDesignOperation extends DesignOperation {

    private boolean merging;

    public boolean isMerging() {
        return merging;
    }

    public void setMerging(boolean merging) {
        this.merging = merging;
    }

    @Override
    final InternalResponse performInternal(Design design, DesignOperation previousOperation) throws UserVisibleMessageException {
        if (merging && previousOperation != null && doMerge(design, previousOperation)) {
            return new InternalResponse(true, design);
        } else {
            doPerform(design);
            return new InternalResponse(false, design);
        }
    }

    @Override
    final Design undoInternal(Design design) throws UserVisibleMessageException {
        doUndo(design);
        return design;
    }

    /**
     * Performs this operation by merging with the previous operation, modifying both the specified design and the
     * previous operation.
     * <p>
     * Returns true on success. This indicates that the operation has been performed on the design, and has been
     * merged into the previous operation such that undoing the previous operation also undoes this operation. In
     * this case, this operation will be discarded afterwards.
     * <p>
     * Returning false means failure to merge, and the contract of this method demands that both the design and the
     * previous operation are left unchanged in that case. This will cause a fallback to {@link #doPerform(Design)}.
     */
    protected boolean doMerge(Design design, DesignOperation previousOperation) throws UserVisibleMessageException {
        return false;
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
