package name.martingeisse.chipdraw.design;

public interface TechnologyRepository {

    Technology getTechnologyOrNull(String id);

    default Technology getTechnology(String id) throws NoSuchTechnologyException {
        Technology technology = getTechnologyOrNull(id);
        if (technology == null) {
            throw new NoSuchTechnologyException(id);
        }
        return technology;
    }

}
