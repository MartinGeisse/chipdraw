package name.martingeisse.chipdraw.operation;

import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

import java.util.LinkedList;

/**
 * Performs operations without undo/redo support. This is not useful for a normal UI but can be used for scripting.
 */
public final class UndoRedoOperationExecutor {

    private Design design;
    private LinkedList<DesignOperation> undoStack = new LinkedList<>();
    private LinkedList<DesignOperation> redoStack = new LinkedList<>();

    public UndoRedoOperationExecutor(Design design) {
        this.design = design;
    }

    public Design getDesign() {
        return design;
    }

    public void perform(DesignOperation operation) throws UserVisibleMessageException {
        if (operation == null) {
            throw new IllegalArgumentException("operation cannot be null");
        }
        redoStack.clear();
        performOrRedoOperation(operation);
    }

    public void undo() throws UserVisibleMessageException {
        if (!undoStack.isEmpty()) {
            DesignOperation operation = undoStack.pop();
            design = operation.undoInternalNullChecked(design);
            redoStack.push(operation);
        }
    }

    public void redo() throws UserVisibleMessageException {
        if (!redoStack.isEmpty()) {
            performOrRedoOperation(redoStack.pop());
        }
    }

    private void performOrRedoOperation(DesignOperation operation) throws UserVisibleMessageException {
        design = operation.performInternalNullChecked(design);
        undoStack.push(operation);
    }

}
