package name.martingeisse.chipdraw.pixel.drc;

/**
 * This interface encapsulates technology-specific design rules.
 */
public interface Drc {

    /**
     * Performs the DRC on the design from the specified context, and using the context to report violations.
     *
     * This method is executed in the DRC background thread.
     */
    void perform(DrcContext context);

    /**
     * An "empty" DRC that can be used for all designs and never reports any violations.
     */
    Drc EMPTY_DRC = new Drc() {
        @Override
        public void perform(DrcContext context) {
        }
    };

}
