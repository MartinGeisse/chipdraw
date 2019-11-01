package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.technology.TechnologyRepository;

import java.io.*;

public final class DesignPersistence {

    public void save(Design design, String path) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(design);
            objectOutputStream.flush();
            System.out.println("saved to: " + path);
        }
    }

    public Design load(String path) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            System.out.println("loading from: " + path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Design design = (Design) objectInputStream.readObject();
            return design;
        } catch (ClassNotFoundException e) {
            throw new IOException("deserialization problem", e);
        }
    }

}
