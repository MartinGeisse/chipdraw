package name.martingeisse.chipdraw.pixel.global_tools.magic;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.global_tools.CornerStitchingExtrator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 *
 */
public class MagicFileIo {

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

}
