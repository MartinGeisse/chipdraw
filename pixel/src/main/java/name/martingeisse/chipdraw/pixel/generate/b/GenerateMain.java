package name.martingeisse.chipdraw.pixel.generate.b;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.global_tools.magic.MagicFileIo;

import java.io.File;

public class GenerateMain {

    private static File folder;

    public static void main(String[] args) throws Exception {
        folder = new File("resource/cell-lib/generated/b");
        if (folder.exists()) {
            folder.delete();
        }
        folder.mkdir();
        generate("Inverter", 1);
        generate("Nand", 2);
        generate("Nor", 1, 1);
    }

    private static void generate(String name, int... mintermArities) throws Exception {
        Integer[] convertedMintermArities = new Integer[mintermArities.length];
        for (int i = 0; i < mintermArities.length; i++) {
            convertedMintermArities[i] = mintermArities[i];
        }
        new Generator(new File(folder, name + ".mag"), convertedMintermArities).generate();
    }

}
