package name.martingeisse.chipdraw.pixel.generate;

public class TestGenerator {

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
