package name.martingeisse.chipdraw.pixel.generate.impl;

import name.martingeisse.chipdraw.pixel.generate.CellBuilder;
import name.martingeisse.chipdraw.pixel.generate.LibraryBuilder;

import java.io.File;

public final class LibraryBuilderImpl implements LibraryBuilder {

    private final File folder;

    public LibraryBuilderImpl(File folder) {
        this.folder = folder;
    }

    @Override
    public CellBuilder newCell(String name) {
        return new CellBuilderImpl(new File(folder, name + ".mag"));
    }

}
