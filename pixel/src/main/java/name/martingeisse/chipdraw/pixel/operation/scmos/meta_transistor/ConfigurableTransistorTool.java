package name.martingeisse.chipdraw.pixel.operation.scmos.meta_transistor;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.operation.DesignOperation;
import name.martingeisse.chipdraw.pixel.operation.SnapshottingDesignOperation;
import name.martingeisse.chipdraw.pixel.operation.mouse.AbstractClickTool;

import java.awt.*;
import java.util.Random;

/**
 *
 */
public final class ConfigurableTransistorTool extends AbstractClickTool {

    private final Material sourceDrainMaterial;
    private final int transistorWidth;
    private final ImmutableList<Integer> gateGroups;
    private final boolean rotated;

    public ConfigurableTransistorTool(Material sourceDrainMaterial, int transistorWidth, ImmutableList<Integer> gateGroups, boolean rotated) {
        this.sourceDrainMaterial = sourceDrainMaterial;
        this.transistorWidth = transistorWidth;
        this.gateGroups = gateGroups;
        this.rotated = rotated;
    }

    @Override
    protected DesignOperation onClick(Design design, int x, int y, MouseButton button, boolean shift) {
        return (button == MouseButton.LEFT) ? createOperation(x, y) : null;
    }

    public DesignOperation createOperation(int x, int y) {
        return new SnapshottingDesignOperation() {
            @Override
            protected void doPerform(Design newDesign) {
                produceRectangles((dx, dy, w, h, material) -> {
                    newDesign.getPlane(material.getPlaneSchema()).drawRectangle(x + dx, y + dy, w, h, material);
                });
            }
        };
    }

    @Override
    public void draw(Graphics2D g, int zoom, boolean shift) {
        Random random = new Random();
        int x = getMouseX();
        int y = getMouseY();
        produceRectangles((dx, dy, w, h, material) -> {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawRect((x + dx) * zoom, (y + dy) * zoom, w * zoom, h * zoom);
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawRect((x + dx) * zoom + 1, (y + dy) * zoom + 1, w * zoom - 2, h * zoom - 2);
        });
    }

    public void produceRectangles(RectangleConsumer consumer) {
        if (rotated) {
			produceUnrotatedRectangles((dx, dy, w, h, material) -> consumer.consume(dy, dx, h, w, material));
		} else {
			produceUnrotatedRectangles(consumer);
		}
    }

    public void produceUnrotatedRectangles(RectangleConsumer consumer) {

    	// some constants to use. Diffusion starts at (0, 0) and poly extends to -2 upwards.
		int metalHeight = transistorWidth * 4;
		int diffusionHeight = metalHeight + 2;
		int polyHeight = diffusionHeight + 4;

    	// initial contacts
    	int totalLength = 5;
		consumer.consume(1, 1, 4, metalHeight, ConceptSchemas.MATERIAL_METAL1);
        for (int j = 0; j < transistorWidth; j++) {
            consumer.consume(2, 2 + 4 * j, 2, 2, ConceptSchemas.MATERIAL_CONTACT);
        }

		for (int gateGroup : gateGroups) {

			// gates
			for (int i = 0; i < gateGroup; i++) {
				consumer.consume(totalLength + 1, -2, 2, polyHeight, ConceptSchemas.MATERIAL_POLY);
				totalLength += 4;
			}

			// contacts
			consumer.consume(totalLength, 1, 4, metalHeight, ConceptSchemas.MATERIAL_METAL1);
            for (int j = 0; j < transistorWidth; j++) {
                consumer.consume(totalLength + 1, 2 + 4 * j, 2, 2, ConceptSchemas.MATERIAL_CONTACT);
            }
			totalLength += 4;

		}

		// add shared source / drain rectangle for all groups
		totalLength++;
		consumer.consume(0, 0, totalLength, diffusionHeight, sourceDrainMaterial);

    }

    public interface RectangleConsumer {
        void consume(int dx, int dy, int width, int height, Material material);
    }

}
