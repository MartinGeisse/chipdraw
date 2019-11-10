package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.design.TechnologyRepository;

public final class Workbench {

    private final TechnologyRepository technologyRepository;

    public Workbench(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    public TechnologyRepository getTechnologyRepository() {
        return technologyRepository;
    }

}
