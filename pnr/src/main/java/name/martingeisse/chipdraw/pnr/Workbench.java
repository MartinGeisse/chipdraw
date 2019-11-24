package name.martingeisse.chipdraw.pnr;

import name.martingeisse.chipdraw.pnr.cell.CellLibraryRepository;

public final class Workbench {

    private final CellLibraryRepository technologyRepository;

    public Workbench(CellLibraryRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    public CellLibraryRepository getTechnologyRepository() {
        return technologyRepository;
    }
    
}
