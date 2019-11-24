package name.martingeisse.chipdraw.pnr;

import name.martingeisse.chipdraw.pnr.design.TechnologyRepository;

public final class Workbench {

    private final TechnologyRepository technologyRepository;

    public Workbench(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    public TechnologyRepository getTechnologyRepository() {
        return technologyRepository;
    }

}
