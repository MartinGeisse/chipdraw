package name.martingeisse.chipdraw.pnr.cell;

public final class NoSuchCellLibraryException extends Exception {

    private final String id;

    public NoSuchCellLibraryException(String id) {
        super("no such cell library: " + id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
