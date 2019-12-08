package name.martingeisse.chipdraw.pixel.global_tools.magic;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
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

		// determine bounds
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

		// TODO

	}

	//endregion

}
