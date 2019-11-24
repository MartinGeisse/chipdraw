package name.martingeisse.chipdraw.pnr.operation;

import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.util.UserVisibleMessageException;

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

    public void perform(DesignOperation operation, boolean merge) throws UserVisibleMessageException {
        if (operation == null) {
            throw new IllegalArgumentException("operation cannot be null");
        }
        redoStack.clear();
        performOrRedoOperation(operation, merge);
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
            performOrRedoOperation(redoStack.pop(), false);
        }
    }

    private void performOrRedoOperation(DesignOperation operation, boolean merge) throws UserVisibleMessageException {
        design = operation.performInternalNullChecked(design);
        if (merge && !undoStack.isEmpty()) {
            DesignOperation previous = undoStack.peek();
            if (previous instanceof MergedDesignOperation) {
                ((MergedDesignOperation) previous).getOperations().add(operation);
            } else {
                undoStack.pop();
                MergedDesignOperation merged = new MergedDesignOperation();
                merged.getOperations().add(previous);
                merged.getOperations().add(operation);
                undoStack.push(merged);
            }
        } else {
            undoStack.push(operation);
        }
    }

}
