package name.martingeisse.chipdraw.pixel.generate;

import java.io.File;

public class GenerateMain {

    public static void main(String[] args) {
        File folder = new File("resource/cell-lib/generated");
        if (folder.exists()) {
            folder.delete();
        }
        folder.mkdir();
        LibraryBuilder libraryBuilder = new LibraryBuilder(folder);
        new TestGenerator().generate(libraryBuilder);
    }

}
