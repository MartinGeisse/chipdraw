package name.martingeisse.chipdraw.pixel.generate.b;

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

    private static void generate(String name, int... mintermArities) {
        Integer[] convertedMintermArities = new Integer[mintermArities.length];
        for (int i = 0; i < mintermArities.length; i++) {
            convertedMintermArities[i] = mintermArities[i];
        }
        new Generator(new File(folder, name), convertedMintermArities).generate();
    }

}
