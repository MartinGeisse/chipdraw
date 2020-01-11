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

        new Generator()
                .pmos(new int[][] {{1}})
                .nmos(new int[][] {{1}})
                .generate(file("Inverter"));
        new Generator()
                .pmos(new int[][] {{1, 1}})
                .nmos(new int[][] {{2}})
                .generate(file("Nand"));
        new Generator()
                .pmos(new int[][] {{2}})
                .nmos(new int[][] {{1, 1}})
                .generate(file("Nor"));
        new Generator()
                .pmos(new int[][] {{2}, {1, 1}})
                .nmos(new int[][] {{1, 1}})
                .generate(file("Foo"));
    }

    private static File file(String name) {
        return new File(folder, name + ".mag");
    }

}
