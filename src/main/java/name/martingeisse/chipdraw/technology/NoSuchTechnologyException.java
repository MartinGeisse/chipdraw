package name.martingeisse.chipdraw.technology;

public final class NoSuchTechnologyException extends Exception {

    private final TechnologyId id;

    public NoSuchTechnologyException(TechnologyId id) {
        super("no such technology: " + id);
        this.id = id;
    }

    public TechnologyId getId() {
        return id;
    }

}
