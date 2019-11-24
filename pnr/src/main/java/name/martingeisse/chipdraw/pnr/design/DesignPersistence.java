package name.martingeisse.chipdraw.pnr.design;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pnr.cell.CellLibraryRepository;
import name.martingeisse.chipdraw.pnr.cell.NoSuchCellLibraryException;

import java.io.*;

public final class DesignPersistence {

    private final CellLibraryRepository cellLibraryRepository;

    public DesignPersistence(CellLibraryRepository cellLibraryRepository) {
        this.cellLibraryRepository = cellLibraryRepository;
    }

    public void save(Design design, String path) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(design);
            objectOutputStream.flush();
            System.out.println("saved to: " + path);
        }
    }

    public Design load(String path) throws IOException, NoSuchCellLibraryException {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            System.out.println("loading from: " + path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            objectInputStream.setObjectInputFilter(this::filter);
            Design design = (Design) objectInputStream.readObject();
            design.initializeAfterDeserialization(cellLibraryRepository);
            return design;
        } catch (ClassNotFoundException e) {
            throw new IOException("deserialization problem", e);
        }
    }

    private ObjectInputFilter.Status filter(ObjectInputFilter.FilterInfo info) {
        Class<?> c = info.serialClass();

        // null is always accepted (no depth test for now)
        if (c == null) {
            return ObjectInputFilter.Status.ALLOWED;
        }

        // application types
        if (c == Design.class || c == RoutingPlane.class) {
            return ObjectInputFilter.Status.ALLOWED;
        }

        // immutable collections
        if (c.getName().equals("com.google.common.collect.ImmutableList$SerializedForm")) {
            return ObjectInputFilter.Status.ALLOWED;
        }
        if (ImmutableList.class.isAssignableFrom(c)) {
            return ObjectInputFilter.Status.ALLOWED;
        }

        // core types
        if (c == Object[].class || c == byte[].class) {
            return ObjectInputFilter.Status.ALLOWED;
        }

        return ObjectInputFilter.Status.REJECTED;
    }

}