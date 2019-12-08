package name.martingeisse.chipdraw.pnr.design;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pnr.cell.CellLibrary;
import name.martingeisse.chipdraw.pnr.cell.CellLibraryRepository;
import name.martingeisse.chipdraw.pnr.cell.NoSuchCellLibraryException;
import name.martingeisse.chipdraw.pnr.util.RectangularSize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class Design implements Serializable, RectangularSize {

    // TODO how do we deal with the number of planes?
    public static final int NUMBER_OF_ROUTING_PLANES = 3;

    private static final long serialVersionUID = 1;

    private final String cellLibraryId;
    private transient CellLibrary cellLibrary;
    private final int width;
    private final int height;
    private final ImmutableList<RoutingPlane> routingPlanes;
    private final CellPlane cellPlane;

    public Design(CellLibrary cellLibrary, int width, int height) {
        this.cellLibraryId = cellLibrary.getId();
        this.cellLibrary = cellLibrary;
        this.width = width;
        this.height = height;

        List<RoutingPlane> routingPlanes = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_ROUTING_PLANES; i++) {
            routingPlanes.add(new RoutingPlane(width, height));
        }
        this.routingPlanes = ImmutableList.copyOf(routingPlanes);
        this.cellPlane = new CellPlane(width, height);
        linkRoutingPlanes();
    }

    public Design(Design original) {
        this(original.getCellLibrary(), original.getWidth(), original.getHeight());
        for (int i = 0; i < routingPlanes.size(); i++) {
            routingPlanes.get(i).copyFrom(original.getRoutingPlanes().get(i));
        }
        // TODO copy cell plane
        linkRoutingPlanes();
    }

    private void linkRoutingPlanes() {
        for (int i = 0; i < routingPlanes.size(); i++) {
            RoutingPlane routingPlane = routingPlanes.get(i);
            routingPlane.design = this;
            routingPlane.index = i;
        }
    }

    void initializeAfterDeserialization(CellLibraryRepository cellLibraryRepository) throws NoSuchCellLibraryException {
        this.cellLibrary = cellLibraryRepository.getCellLibrary(cellLibraryId);
    }

    public CellLibrary getCellLibrary() {
        return cellLibrary;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImmutableList<RoutingPlane> getRoutingPlanes() {
        return routingPlanes;
    }

    public int getRoutingPlaneCount() {
        return routingPlanes.size();
    }

    public int getTotalPlaneCount() {
        return getRoutingPlaneCount() + 1;
    }

    public CellPlane getCellPlane() {
        return cellPlane;
    }

    public void copyFrom(Design source, int sourceX, int sourceY, int destinationX, int destinationY, int rectangleWidth, int rectangleHeight) {
        if (source.getCellLibrary() != getCellLibrary()) {
            throw new IllegalArgumentException("cannot copy from design with different cell library");
        }
        for (int i = 0; i < routingPlanes.size(); i++) {
            routingPlanes.get(i).copyFrom(source.getRoutingPlanes().get(i), sourceX, sourceY, destinationX, destinationY, rectangleWidth, rectangleHeight);
        }
        // TODO copy cell plane
    }

}
