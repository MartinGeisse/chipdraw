package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.operation.DesignOperation;
import name.martingeisse.chipdraw.operation.UndoRedoOperationExecutor;
import name.martingeisse.chipdraw.drc.DrcAgent;
import name.martingeisse.chipdraw.drc.Violation;
import name.martingeisse.chipdraw.technology.Technologies;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

/**
 * Represents the state of the design editor in an abstract, UI-independent way. This class keeps the design as well as
 * the following features:
 * - undo support
 * - DRC
 */
public class Editor {

    private final Ui ui;
    private final DrcAgent drcAgent;

    private UndoRedoOperationExecutor operationExecutor;
    private ImmutableList<Violation> drcViolations;

    public Editor(Ui ui) {
        if (ui == null) {
            throw new IllegalArgumentException("ui cannot be null");
        }
        this.ui = ui;
        this.drcAgent = new DrcAgent();
        this.operationExecutor = new UndoRedoOperationExecutor(new Design(Technologies.CONCEPT, 1, 1));
        drcAgent.addResultListener(this::consumeDrcResult);
    }

    public void dispose() {
        drcAgent.dispose();
    }

    public Design getDesign() {
        return operationExecutor.getDesign();
    }

    public void restart(Design design) {
        if (design == null) {
            throw new IllegalArgumentException("design cannot be null");
        }
        this.operationExecutor = new UndoRedoOperationExecutor(design);
        ui.onRestart();
        consumeDrcResult(null);
        drcAgent.setDesign(design);
    }

    public ImmutableList<Violation> getDrcViolations() {
        return drcViolations;
    }

    public void performOperation(DesignOperation operation) throws UserVisibleMessageException {
        Design oldDesign = getDesign();
        operationExecutor.perform(operation);
        afterModification(oldDesign);
    }

    public void undo() throws UserVisibleMessageException {
        Design oldDesign = getDesign();
        operationExecutor.undo();
        afterModification(oldDesign);
    }

    public void redo() throws UserVisibleMessageException {
        Design oldDesign = getDesign();
        operationExecutor.redo();
        afterModification(oldDesign);
    }

    private void afterModification(Design oldDesign) {
        Design newDesign = operationExecutor.getDesign();
        if (newDesign == oldDesign) {
            ui.onDesignModified();
            consumeDrcResult(null);
            drcAgent.trigger();
        } else {
            ui.onDesignObjectReplaced();
            consumeDrcResult(null);
            drcAgent.setDesign(newDesign);
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
         * <p>
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
