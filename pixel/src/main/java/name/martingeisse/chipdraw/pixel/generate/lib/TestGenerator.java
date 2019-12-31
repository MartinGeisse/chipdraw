package name.martingeisse.chipdraw.pixel.generate.lib;

import name.martingeisse.chipdraw.pixel.generate.CellBuilder;
import name.martingeisse.chipdraw.pixel.generate.LibraryBuilder;
import name.martingeisse.chipdraw.pixel.generate.LibraryGenerator;

public class TestGenerator implements LibraryGenerator {

    @Override
    public void generate(LibraryBuilder libraryBuilder) {
        {
            CellBuilder builder = libraryBuilder.newCell("InverterX1");
            builder.build();
        }
        {
            CellBuilder builder = libraryBuilder.newCell("InverterX2");
            builder.build();
        }
    }

}
