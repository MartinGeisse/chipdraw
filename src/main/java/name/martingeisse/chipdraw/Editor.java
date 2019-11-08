package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.DesignOperation;
import name.martingeisse.chipdraw.design.UndoEntry;
import name.martingeisse.chipdraw.drc.DrcAgent;
import name.martingeisse.chipdraw.drc.Violation;
import name.martingeisse.chipdraw.technology.Technologies;

import java.util.LinkedList;

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
    private LinkedList<UndoEntry> undoStack = new LinkedList<>();
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

    public void restart(Design design) {
        if (design == null) {
            throw new IllegalArgumentException("design cannot be null");
        }
        this.design = design;
        ui.onRestart();
        consumeDrcResult(null);
        drcAgent.setDesign(design);
    }

    public void setDesign(Design design) {
        if (design == null) {
            throw new IllegalArgumentException("design cannot be null");
        }
        this.design = design;
        ui.onDesignObjectReplaced();
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
        DesignOperation.Result result = operation.perform(design);
        if (result == null) {
            throw new RuntimeException("operation returned null");
        }
        if (result.undoEntry == null) {
            undoStack.clear();
        } else {
            undoStack.add(result.undoEntry);
        }
        afterOperationOrUndo(result.newDesign);
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        UndoEntry entry = undoStack.removeLast();
        afterOperationOrUndo(entry.perform(design));
    }

    private void afterOperationOrUndo(Design newDesign) {
        if (newDesign != null) {
            setDesign(newDesign);
        } else {
            ui.onDesignModified();
            consumeDrcResult(null);
            drcAgent.trigger();
        }
    }

    private void consumeDrcResult(ImmutableList<Violation> violations) {
        this.drcViolations = violations;
        ui.consumeDrcResult(violations);
    }

    public interface Ui {

        /**
         * Called when the user logically starts editing a different design. This should expect the {@link Design}
         * object of the editor to be replaced, and also reset the UI state, e.g. zoom level, toolbars, and so on.
         */
        void onRestart();

        /**
         * Called when the {@link Design} object has been replaced by another one, but the user is logically still
         * editing the "same" design. That is, the UI state (e.g. zoom level) should be kept intact, and the editor
         * also keeps editor state such as the undo stack intact. This case happens when an operation has radical
         * effects such as changing the size or technology of the design.
         *
         * (Note: It is unclear whether we will keep this kind of change in the future, or rather make the
         * {@link Design} class handle even radical changes internally).
         */
        void onDesignObjectReplaced();

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
