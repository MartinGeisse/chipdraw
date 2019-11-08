package name.martingeisse.chipdraw.design;

/**
 * Implementations must be immutable.
 */
public interface Undoer {

    /**
     * This method assumes that the argument design is in the same state as just after the original operation has been
     * performed. Either removes the effects of the original operation from that design, or returns an (old) design
     * that is to be used instead.
     */
    Design perform(Design design);

}
