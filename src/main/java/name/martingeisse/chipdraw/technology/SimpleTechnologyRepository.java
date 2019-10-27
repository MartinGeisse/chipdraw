package name.martingeisse.chipdraw.technology;

import java.util.HashMap;

public final class SimpleTechnologyRepository implements TechnologyRepository {

    private final HashMap<TechnologyId, Technology> technologies = new HashMap<>();

    public void add(Technology technology) {
        technologies.put(technology.getId(), technology);
    }

    @Override
    public Technology getTechnologyOrNull(TechnologyId id) {
        return technologies.get(id);
    }

}
