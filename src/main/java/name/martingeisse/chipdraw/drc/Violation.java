package name.martingeisse.chipdraw.drc;

public class Violation {

    private final String message;

    public Violation(String message) {
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }

    public String getFullText() {
        return message;
    }

}
