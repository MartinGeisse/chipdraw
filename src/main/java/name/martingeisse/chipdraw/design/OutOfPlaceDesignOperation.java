package name.martingeisse.chipdraw.design;

import name.martingeisse.chipdraw.util.UserVisibleMessageException;

/**
 * This class acts by constructing a new design from the old one, leaving the old one unchanged. Undo support simply
 * restores the old design.
 */
public abstract class OutOfPlaceDesignOperation extends DesignOperation {

    private Design oldDesign;

    @Override
    final InternalResponse performInternal(Design oldDesign, DesignOperation previousOperation) throws UserVisibleMessageException {
        this.oldDesign = oldDesign;
        Design newDesign = createNewDesign(oldDesign);
        return new InternalResponse(false, newDesign);
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
