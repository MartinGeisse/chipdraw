package name.martingeisse.chipdraw.drc;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.Design;

import java.util.ArrayList;
import java.util.List;

public final class DrcContext {

    private final Design design;
    private final List<Violation> violations = new ArrayList<>();

    public DrcContext(Design design) {
        this.design = design;
    }

    public Design getDesign() {
        return design;
    }

    public ImmutableList<Violation> getViolations() {
        return ImmutableList.copyOf(violations);
    }

    public void report(Violation violation) {
        violations.add(violation);
    }

    public void report(String message) {
        report(new Violation(message));
    }

    public void report(int x, int y, String message) {
        report(new PositionedViolation(message, x, y));
    }

}
