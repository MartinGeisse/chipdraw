package name.martingeisse.chipdraw.pixel.generate.a;

import java.io.File;

public class GenerateMain {

    public static void main(String[] args) throws Exception {
        File folder = new File("resource/cell-lib/generated/a");
        if (folder.exists()) {
            folder.delete();
        }
        folder.mkdir();
        LibraryBuilder libraryBuilder = new LibraryBuilder(folder);
        new TestGenerator().generate(libraryBuilder);
    }

}
