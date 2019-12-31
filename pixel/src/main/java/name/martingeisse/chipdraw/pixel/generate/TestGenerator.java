package name.martingeisse.chipdraw.pixel.generate;

public class TestGenerator {

    public void generate(LibraryBuilder libraryBuilder) throws Exception {
        {
            CellBuilder builder = libraryBuilder.newCell("InverterX1");
            builder.addNmosElement(new TransistorElement());
            builder.addPmosElement(new TransistorElement().setSize(2));
            builder.build();
        }
        {
            CellBuilder builder = libraryBuilder.newCell("InverterX2");
            builder.addNmosElement(new TransistorElement().setSize(2));
            builder.addPmosElement(new TransistorElement().setSize(4));
            builder.build();
        }
    }

}
