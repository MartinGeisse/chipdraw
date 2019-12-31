package name.martingeisse.chipdraw.pixel.generate;

import name.martingeisse.chipdraw.pixel.generate.impl.LibraryBuilderImpl;
import name.martingeisse.chipdraw.pixel.generate.lib.TestGenerator;

import java.io.File;

public class GenerateMain {

    public static void main(String[] args) {
        File folder = new File("resource/cell-lib/generated");
        if (folder.exists()) {
            folder.delete();
        }
        folder.mkdir();
        LibraryBuilderImpl libraryBuilder = new LibraryBuilderImpl(folder);
        new TestGenerator().generate(libraryBuilder);
    }

}
