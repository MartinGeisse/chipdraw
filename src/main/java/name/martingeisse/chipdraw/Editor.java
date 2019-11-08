package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.DesignOperation;
import name.martingeisse.chipdraw.design.UndoEntry;

/**
 * Represents the state of the design editor in an abstract, UI-independent way. This class keeps the design as well as
 * the following features:
 * - undo support
 *
 * Note that changes to the design must be performed by passing a {@link DesignOperation} to this editor, NOT by
 * performing the operation directly on the design or even calling setters on the design. If that were done, these
 * changes would corrupt the undo stack, and the UI and other listeners would not be informed properly.
 * TODO some code currently does this
 */
public class Editor {

    private final Ui ui;
    private Design design;

    public Editor(Ui ui, Design design) {
        if (ui == null) {
            throw new IllegalArgumentException("ui cannot be null");
        }
        if (design == null) {
            throw new IllegalArgumentException("design cannot be null");
        }
        this.ui = ui;
        this.design = design;
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
    }

    public void performOperation(DesignOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation cannot be null");
        }
        UndoEntry undoEntry = operation.perform(design);
        // TODO undo stack
        ui.onDesignModified();
    }

    public interface Ui {

        /**
         * Called when the {@link Design} object has been replaced by another one.
         */
        void onDesignReplaced();

        /**
         * Called when the contents of the design have changed.
         */
        void onDesignModified();

    }

}
