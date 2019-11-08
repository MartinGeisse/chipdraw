package name.martingeisse.chipdraw.design;

/**
 * Implementations must be immutable.
 */
public interface DesignOperation {

    /**
     * Performs this operation on the specified design.
     *
     * If this operation is undo-able, then this method returns an {@link UndoEntry} that can be used later to undo
     * the operation. This entry stores enough information to change the new state of the design back to the old
     * state. The undo entry should not keep a reference to the design -- a reference will be passed to the undo
     * operation returned from the undo entry, which should be used instead.
     *
     * If this operation is not undo-able, this method returns null.
     */
    UndoEntry perform(Design design);

}
