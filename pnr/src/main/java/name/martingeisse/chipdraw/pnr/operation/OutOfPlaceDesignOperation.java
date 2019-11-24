package name.martingeisse.chipdraw.pnr.operation;

import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.util.UserVisibleMessageException;

/**
 * This class acts by constructing a new design from the old one, leaving the old one unchanged. Undo support simply
 * restores the old design.
 */
public abstract class OutOfPlaceDesignOperation extends DesignOperation {

    private Design oldDesign;

    @Override
    final Design performInternal(Design oldDesign) throws UserVisibleMessageException {
        this.oldDesign = oldDesign;
        Design newDesign = createNewDesign(oldDesign);
        if (newDesign == null) {
            throw new RuntimeException("createNewDesign() returned null");
        }
        return newDesign;
    }

    @Override
    final Design undoInternal(Design newDesign) throws UserVisibleMessageException {
        return oldDesign;
    }

    /**
     * Performs this operation by creating a new design from the old one, leaving the old one unchanged.
     */
    protected abstract Design createNewDesign(Design oldDesign) throws UserVisibleMessageException;

}
