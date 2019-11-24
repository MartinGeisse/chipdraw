package name.martingeisse.chipdraw.pnr.cell;

public interface CellLibraryRepository {

    CellLibrary getCellLibraryOrNull(String id);

    default CellLibrary getCellLibrary(String id) throws NoSuchCellLibraryException {
        CellLibrary cellLibrary = getCellLibraryOrNull(id);
        if (cellLibrary == null) {
            throw new NoSuchCellLibraryException(id);
        }
        return cellLibrary;
    }

}
