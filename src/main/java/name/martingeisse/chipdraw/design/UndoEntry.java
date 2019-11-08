package name.martingeisse.chipdraw.design;

/**
 * Implementations must be immutable.
 */
public interface UndoEntry {

    /**
     * Removes the effects of the original operation from the specified design, assuming that it is in the samme
     * state as just after the original operation has been performed.
     */
    void perform(Design design);

}
