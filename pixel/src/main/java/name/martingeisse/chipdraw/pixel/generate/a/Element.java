package name.martingeisse.chipdraw.pixel.generate.a;

import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.operation.SimpleOperationExecutor;

public interface Element {

    int getWidth();

    int getHeight();

    int getRailSpacing();

    void draw(SimpleOperationExecutor executor, int x, int y, Material diffusionMaterial) throws Exception;

}
