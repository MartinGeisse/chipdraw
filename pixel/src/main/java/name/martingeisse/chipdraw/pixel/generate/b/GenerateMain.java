package name.martingeisse.chipdraw.pixel.generate.b;

import java.io.File;

public class GenerateMain {

    public static void main(String[] args) throws Exception {
        File folder = new File("resource/cell-lib/generated/b");
        if (folder.exists()) {
            folder.delete();
        }
        folder.mkdir();
        generate("Inverter", new int[][] {{0}});
        generate("Nand", new int[][] {{0, 1}});
        generate("Nor", new int[][] {{0}, {1}});
    }

    private static void generate(String name, int[][] minterms) {

    }

}
