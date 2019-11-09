package name.martingeisse.chipdraw.operation;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

/**
 * This class provides undo/redo support by making a snapshot of the whole design before modifying it. This is a
 * simplified version of {@link OutOfPlaceDesignOperation} which assumes that the changes can be applied in-place to an
 * identical copy of the original design.
 */
public abstract class SnapshottingDesignOperation extends OutOfPlaceDesignOperation {

    @Override
    protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
        Design newDesign = new Design(oldDesign);
        doPerform(newDesign);
        return newDesign;
    }

    /**
     * Performs this operation by modifying the specified design, which is a copy of the original design.
     */
    protected abstract void doPerform(Design design) throws UserVisibleMessageException;

}
