package name.martingeisse.chipdraw.pnr.operation;

import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.util.UserVisibleMessageException;

/**
 * Performs operations without undo/redo support. This is not useful for a normal UI but can be used for scripting.
 */
public final class SimpleOperationExecutor {

    private Design design;

    public SimpleOperationExecutor(Design design) {
        this.design = design;
    }

    public Design getDesign() {
        return design;
    }

    public void perform(DesignOperation operation) throws UserVisibleMessageException {
        design = operation.performInternalNullChecked(design);
    }

}
