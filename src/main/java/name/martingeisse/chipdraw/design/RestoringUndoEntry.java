package name.martingeisse.chipdraw.design;

public final class RestoringUndoEntry implements UndoEntry {

    private final Design oldDesign;

    public RestoringUndoEntry(Design oldDesign) {
        this.oldDesign = oldDesign;
    }

    @Override
    public Design perform(Design design) {
        return oldDesign;
    }

}
