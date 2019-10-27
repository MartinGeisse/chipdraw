package name.martingeisse.chipdraw.technology;

public interface TechnologyRepository {

    Technology getTechnologyOrNull(TechnologyId id);

    default Technology getTechnology(TechnologyId id) throws NoSuchTechnologyException {
        Technology technology = getTechnologyOrNull(id);
        if (technology == null) {
            throw new NoSuchTechnologyException(id);
        }
        return technology;
    }

}
