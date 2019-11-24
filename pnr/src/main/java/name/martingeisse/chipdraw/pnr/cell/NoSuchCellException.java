package name.martingeisse.chipdraw.pnr.cell;

public final class NoSuchCellException extends Exception {

    private final String id;

    public NoSuchCellException(String id) {
        super("no such cell: " + id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
