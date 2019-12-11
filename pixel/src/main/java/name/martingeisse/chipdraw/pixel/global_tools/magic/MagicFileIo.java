package name.martingeisse.chipdraw.pixel.global_tools.magic;

import name.martingeisse.chipdraw.pixel.design.*;
import name.martingeisse.chipdraw.pixel.global_tools.CornerStitchingExtrator;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;
import org.apache.commons.lang3.mutable.MutableInt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 *
 */
public final class MagicFileIo {

	private MagicFileIo() {
	}

	//region writing

	public static void write(Design design, File file, String techSpecifier) throws IOException {
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.ISO_8859_1)) {
				try (PrintWriter out = new PrintWriter(outputStreamWriter)) {
					out.println("magic");
					out.println("tech " + techSpecifier);
					out.println("timestamp " + new Date().getTime());
					for (Plane plane : design.getPlanes()) {
						for (Material outputMaterial : plane.getSchema().getMaterials()) {
							if (plane.hasMaterial(outputMaterial)) {
								out.println("<< " + outputMaterial.getName() + " >>");
								new CornerStitchingExtrator() {
									@Override
									protected void finishRectangle(Material rectangleMaterial, int x, int y, int width, int height) {
										if (rectangleMaterial == outputMaterial) {
											int flippedY1 = design.getHeight() - y - height;
											int flippedY2 = design.getHeight() - y;
											out.println("rect " + x + " " + flippedY1 + " " + (x + width) + " " + flippedY2);
										}
									}
								}.extract(plane);
							}
						}
					}
					out.println("<< end >>");
				}
			}
		}
	}

	//endregion

	//region reading

	public static Design read(File file) throws IOException, UserVisibleMessageException {

		// create empty design and determine coordinate offset
		Design design;
		int dx, dy;
		{
			MutableInt minX = new MutableInt(Integer.MAX_VALUE);
			MutableInt minY = new MutableInt(Integer.MAX_VALUE);
			MutableInt maxX = new MutableInt(Integer.MIN_VALUE);
			MutableInt maxY = new MutableInt(Integer.MIN_VALUE);
			new MagicFileReadHelper(file) {
				@Override
				protected void handleRectLine(int x1, int y1, int x2, int y2) {
					minX.setValue(Math.min(minX.getValue(), x1));
					minY.setValue(Math.min(minY.getValue(), y1));
					maxX.setValue(Math.max(maxX.getValue(), x2));
					maxY.setValue(Math.max(maxY.getValue(), y2));
				}
			}.read();
			if (minX.getValue() >= maxX.getValue() || minY.getValue() >= maxY.getValue()) {
				throw new UserVisibleMessageException("design is empty");
			}
			dx = minX.getValue();
			dy = minY.getValue();
			design = new Design(Technologies.Concept.TECHNOLOGY, maxX.getValue() - dx, maxY.getValue() - dy);
		}

		// read design
		new MagicFileReadHelper(file) {

			private PlaneSchema planeSchema;
			private Material material;
			private Plane plane;

			@Override
			protected void handleSectionLine(String section) throws UserVisibleMessageException {
				if (section.equals("end")) {
					planeSchema = null;
					material = null;
					plane = null;
					return;
				}
				for (PlaneSchema planeSchema : design.getTechnology().getPlaneSchemas()) {
					for (Material material : planeSchema.getMaterials()) {
						if (material.getName().equals(section)) {
							this.planeSchema = planeSchema;
							this.material = material;
							this.plane = design.getPlane(planeSchema);
							return;
						}
					}
				}
				throw new UserVisibleMessageException("unexpected section: " + section);
			}

			@Override
			protected void handleRectLine(int x1, int y1, int x2, int y2) throws UserVisibleMessageException {
				// TODO inclusive / exclusive ?
				// TODO y-flip
				if (plane == null) {
					throw new UserVisibleMessageException("rectangle without material");
				}
				if (x1 > x2) {
					int temp = x1;
					x1 = x2;
					x2 = temp;
				}
				if (y1 > y2) {
					int temp = y1;
					y1 = y2;
					y2 = temp;
				}
				plane.drawRectangle(x1 - dx, y1 - dy, x2 - x1, y2 - y1, material);
			}

		}.read();

		return design;
	}

	//endregion

}
