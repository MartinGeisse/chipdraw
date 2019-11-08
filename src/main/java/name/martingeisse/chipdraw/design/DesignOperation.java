package name.martingeisse.chipdraw.design;

import name.martingeisse.chipdraw.util.UserVisibleMessageException;

/**
 * Implementations must be immutable.
 */
public interface DesignOperation {

    /**
     * Performs this operation on the specified design.
     *
     * If this operation is undo-able, then the result returned by this method contains an {@link Undoer} that can
     * be used later to undo the operation. This entry stores enough information to change the new state of the design
     * back to the old state. The undo entry should not keep a reference to the design -- a reference will be passed to
     * the undo entry, which should be used instead.
     *
     * If this operation is not undo-able, the undo entry in the result is null.
     *
     * Instead of an undo entry, the result may contain a new {@link Design} instance that is to be used instead of the
     * old one. This is useful if the changes to the design are too radical, so building a new object is easier. In
     * this case, the old design instance should not be modified, and an undo entry will be built automatically that
     * simply restores the old design.
     */
    Result perform(Design design) throws UserVisibleMessageException;

    /**
     * Represents the result of an operation.
     */
    class Result {

        public final Undoer undoer;
        public final Design newDesign;

        /**
         * Result for an operation that modifies the original design in-place and cannot be undone.
         */
        public Result() {
            this.undoer = null;
            this.newDesign = null;
        }

        /**
         * Result for an operation that modifies the original design in-place that can be undone.
         */
        public Result(Undoer undoer) {
            this.undoer = undoer;
            this.newDesign = null;
        }

        /**
         * Result for an operation that returns a new design and can be undone implicitly by changing back to
         * the old design.
         */
        public Result(Design oldDesign, Design newDesign) {
            this.undoer = new RestoringUndoer(oldDesign);
            this.newDesign = newDesign;
        }

    }

}
