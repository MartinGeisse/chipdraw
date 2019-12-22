package name.martingeisse.chipdraw.pixel.operation.mouse;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.operation.DesignOperation;

public abstract class AbstractClickTool implements MouseTool {

    private int mouseX, mouseY;

    @Override
    public final Result onMousePressed(Design design, int x, int y, MouseButton button, boolean shift) {
        this.mouseX = x;
        this.mouseY = y;
        DesignOperation operation = onClick(design, x, y, button, shift);
        return (operation == null ? null : new Result(operation, false));
    }

    @Override
    public final Result onMouseMoved(Design design, int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        return null;
    }

    @Override
    public final Result onMouseReleased(Design design) {
        return null;
    }

    public final int getMouseX() {
        return mouseX;
    }

    public final int getMouseY() {
        return mouseY;
    }

    protected abstract DesignOperation onClick(Design design, int x, int y, MouseButton button, boolean shift);

}
