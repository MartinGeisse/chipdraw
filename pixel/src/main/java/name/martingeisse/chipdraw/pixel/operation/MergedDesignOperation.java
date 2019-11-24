package name.martingeisse.chipdraw.pixel.operation;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

import java.util.ArrayList;
import java.util.List;

final class MergedDesignOperation extends DesignOperation {

    private final List<DesignOperation> operations = new ArrayList<>();

    public List<DesignOperation> getOperations() {
        return operations;
    }

    @Override
    Design performInternal(Design design) throws UserVisibleMessageException {
        for (DesignOperation operation : operations) {
            design = operation.performInternalNullChecked(design);
        }
        return design;
    }

    @Override
    Design undoInternal(Design design) throws UserVisibleMessageException {
        for (int i = operations.size() - 1; i >= 0; i--) {
            design = operations.get(i).undoInternalNullChecked(design);
        }
        return design;
    }

}
