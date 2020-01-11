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
                .post(c -> c.directConnectGatesWithPort(0, 1, 4))
                .generate(file("Inverter"));
        new Generator()
                .pmos(new int[][] {{1, 1}})
                .nmos(new int[][] {{2}})
                .post(c -> {
                    c.directConnectGatesWithPort(0, 1, 4);
                    c.directConnectGatesWithPort(1, 2, 4, 1, 0);
                })
                .generate(file("Nand"));
        new Generator()
                .pmos(new int[][] {{2}})
                .nmos(new int[][] {{1, 1}})
                .post(c -> {
                    c.directConnectGatesWithPort(0, 1, 4);
                    c.directConnectGatesWithPort(1, 2, 4, 1, 0);
                })
                .generate(file("Nor"));
    }

    private static File file(String name) {
        return new File(folder, name + ".mag");
    }

}
