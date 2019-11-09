package name.martingeisse.chipdraw.design;

import name.martingeisse.chipdraw.util.UserVisibleMessageException;

/**
 * Implementations cannot be re-used to perform an operation multiple times -- the state of this object is stored
 * on the undo stack.
 *
 * This class cannot be used directly. Use one of the subclasses to implement an operation.
 *
 * TODO SimpleOperationExecutor without undo/redo, UndoRedoOperationExecutor with u/r -- those can be used by callers
 */
public abstract class DesignOperation {

    DesignOperation() {
    }

    abstract InternalResponse performInternal(Design design, DesignOperation previousOperation) throws UserVisibleMessageException;

    abstract Design undoInternal(Design design) throws UserVisibleMessageException;

    /**
     * Represents the result of an operation.
     */
    class InternalResponse {

        final boolean merged;
        final Design newDesign;

        public InternalResponse(boolean merged, Design newDesign) {
            if (newDesign == null) {
                throw new IllegalArgumentException("newDesign cannot be null");
            }
            this.merged = merged;
            this.newDesign = newDesign;
        }

    }

}
