package name.martingeisse.chipdraw.pixel.generate;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.global_tools.magic.MagicFileIo;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellTemplateGeneratorBase;

import java.io.File;
import java.io.IOException;

public final class CellBuilder {

    private final File file;

    public CellBuilder(File file) {
        this.file = file;
    }

    public void build() {
        StandardCellTemplateGeneratorBase templateGenerator = new StandardCellTemplateGeneratorBase();
        templateGenerator.setWidth(21);
        Design design = templateGenerator.generate();
        try {
            MagicFileIo.write(design, file, design.getTechnology().getId(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
