package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.DesignOperation;
import name.martingeisse.chipdraw.design.UndoEntry;
import name.martingeisse.chipdraw.drc.DrcAgent;
import name.martingeisse.chipdraw.drc.Violation;
import name.martingeisse.chipdraw.technology.Technologies;

/**
 * Represents the state of the design editor in an abstract, UI-independent way. This class keeps the design as well as
 * the following features:
 * - undo support
 * - DRC
 * <p>
 * Note that changes to the design must be performed by passing a {@link DesignOperation} to this editor, NOT by
 * performing the operation directly on the design or even calling setters on the design. If that were done, these
 * changes would corrupt the undo stack, and the UI and other listeners would not be informed properly.
 * TODO some code currently does this
 */
public class Editor {

    private final Ui ui;
    private final DrcAgent drcAgent;

    private Design design;
    private ImmutableList<Violation> drcViolations;

    public Editor(Ui ui) {
        if (ui == null) {
            throw new IllegalArgumentException("ui cannot be null");
        }
        this.ui = ui;
        this.drcAgent = new DrcAgent();
        this.design = new Design(Technologies.CONCEPT, 1, 1);
        drcAgent.addResultListener(this::consumeDrcResult);
    }

    public void dispose() {
        drcAgent.dispose();
    }

    public Design getDesign() {
        return design;
    }

    public void setDesign(Design design) {
        if (design == null) {
            throw new IllegalArgumentException("design cannot be null");
        }
        this.design = design;
        ui.onDesignReplaced();
        consumeDrcResult(null);
        drcAgent.setDesign(design);
    }

    public ImmutableList<Violation> getDrcViolations() {
        return drcViolations;
    }

    public void performOperation(DesignOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation cannot be null");
        }
        UndoEntry undoEntry = operation.perform(design);
        // TODO undo stack
        ui.onDesignModified();
        consumeDrcResult(null);
        drcAgent.trigger();
    }

    private void consumeDrcResult(ImmutableList<Violation> violations) {
        this.drcViolations = violations;
        ui.consumeDrcResult(violations);
    }

    public interface Ui {

        /**
         * Called when the {@link Design} object has been replaced by another one.
         * <p>
         * NOTE clarify the relation between the Design object and logcially editing the same/a different design,
         * but only do this after moving logic to this editor class.
         */
        void onDesignReplaced();

        /**
         * Called when the contents of the design have changed.
         */
        void onDesignModified();

        /**
         * Consumes the DRC result: null means DRC still running, empty means no errors, otherwise the list contains
         * the errors.
         */
        void consumeDrcResult(ImmutableList<Violation> violations);

    }

}
