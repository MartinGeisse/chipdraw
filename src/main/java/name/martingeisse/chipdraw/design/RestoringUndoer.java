package name.martingeisse.chipdraw.design;

public final class RestoringUndoer implements Undoer {

    private final Design oldDesign;

    public RestoringUndoer(Design oldDesign) {
        this.oldDesign = oldDesign;
    }

    @Override
    public Design perform(Design design) {
        return oldDesign;
    }

}
