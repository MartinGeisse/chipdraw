package name.martingeisse.chipdraw.design;

import java.io.*;

public final class DesignPersistence {

    private final TechnologyRepository technologyRepository;

    public DesignPersistence(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    public void save(Design design, String path) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(design);
            objectOutputStream.flush();
            System.out.println("saved to: " + path);
        }
    }

    public Design load(String path) throws IOException, NoSuchTechnologyException {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            System.out.println("loading from: " + path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Design design = (Design) objectInputStream.readObject();
            design.initializeAfterDeserialization(technologyRepository);
            return design;
        } catch (ClassNotFoundException e) {
            throw new IOException("deserialization problem", e);
        }
    }

}
