package name.martingeisse.chipdraw.pixel.generate;

import java.io.File;

public final class LibraryBuilder {

    private final File folder;

    public LibraryBuilder(File folder) {
        this.folder = folder;
    }

    public CellBuilder newCell(String name) {
        return new CellBuilder(new File(folder, name + ".mag"));
    }

}
