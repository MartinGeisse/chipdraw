package name.martingeisse.chipdraw.pixel;

import name.martingeisse.chipdraw.pixel.design.TechnologyRepository;

public final class Workbench {

    private final TechnologyRepository technologyRepository;

    public Workbench(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    public TechnologyRepository getTechnologyRepository() {
        return technologyRepository;
    }

}
