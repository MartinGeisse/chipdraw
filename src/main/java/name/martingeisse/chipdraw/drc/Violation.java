package name.martingeisse.chipdraw.drc;

public class Violation {

    private final String message;

    public Violation(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }
        message = message.trim();
        if (message.isEmpty()) {
            throw new IllegalArgumentException("DRC violation message cannot be empty");
        }
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }

    public String getFullText() {
        return message;
    }

}
