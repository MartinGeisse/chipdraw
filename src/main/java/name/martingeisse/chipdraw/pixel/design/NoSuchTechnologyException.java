package name.martingeisse.chipdraw.pixel.design;

public final class NoSuchTechnologyException extends Exception {

    private final String id;

    public NoSuchTechnologyException(String id) {
        super("no such technology: " + id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
