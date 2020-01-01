package name.martingeisse.chipdraw.pixel.generate;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Technologies;
import name.martingeisse.chipdraw.pixel.operation.SimpleOperationExecutor;
import name.martingeisse.chipdraw.pixel.operation.scmos.meta_transistor.ConfigurableTransistorTool;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public class TransistorElement implements Element {

    private int size = 1;
    private boolean rotated = false;
    private ImmutableList<Integer> gateGroups = ImmutableList.of(1);
    private ImmutableList<Integer> powerConnectedContacts = ImmutableList.of();

    public int getSize() {
        return size;
    }

    public TransistorElement setSize(int size) {
        this.size = size;
        return this;
    }

    public boolean isRotated() {
        return rotated;
    }

    public TransistorElement setRotated(boolean rotated) {
        this.rotated = rotated;
        return this;
    }

    public ImmutableList<Integer> getGateGroups() {
        return gateGroups;
    }

    public TransistorElement setGateGroups(List<Integer> gateGroups) {
        this.gateGroups = ImmutableList.copyOf(gateGroups);
        return this;
    }

    public TransistorElement setGateGroups(Integer... gateGroups) {
        this.gateGroups = ImmutableList.copyOf(gateGroups);
        return this;
    }

    public ImmutableList<Integer> getPowerConnectedContacts() {
        return powerConnectedContacts;
    }

    public TransistorElement setPowerConnectedContacts(List<Integer> powerConnectedContacts) {
        this.powerConnectedContacts = ImmutableList.copyOf(powerConnectedContacts);
        return this;
    }

    public TransistorElement setPowerConnectedContacts(Integer... powerConnectedContacts) {
        this.powerConnectedContacts = ImmutableList.copyOf(powerConnectedContacts);
        return this;
    }

    @Override
    public int getWidth() {
        MutableInt result = new MutableInt(0);
        ConfigurableTransistorTool tool = new ConfigurableTransistorTool(Technologies.Concept.MATERIAL_NDIFF, size, gateGroups, rotated);
        tool.produceRectangles((dx, dy, w, h, material) -> {
            if (material == Technologies.Concept.MATERIAL_NDIFF) {
                result.setValue(w);
            }
        });
        return result.getValue();
    }

    @Override
    public int getHeight() {
        MutableInt result = new MutableInt(0);
        ConfigurableTransistorTool tool = new ConfigurableTransistorTool(Technologies.Concept.MATERIAL_NDIFF, size, gateGroups, rotated);
        tool.produceRectangles((dx, dy, w, h, material) -> {
            if (material == Technologies.Concept.MATERIAL_NDIFF) {
                result.setValue(h);
            }
        });
        return result.getValue();
    }

    @Override
    public int getRailSpacing() {
        return 1;
    }

    @Override
    public void draw(SimpleOperationExecutor executor, int x, int y, Material diffusionMaterial) throws Exception {
        ConfigurableTransistorTool tool = new ConfigurableTransistorTool(diffusionMaterial, size, gateGroups, rotated);
        executor.perform(tool.createOperation(x, y));
    }

}
