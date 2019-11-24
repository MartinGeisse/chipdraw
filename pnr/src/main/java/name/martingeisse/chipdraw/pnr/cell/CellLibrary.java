package name.martingeisse.chipdraw.pnr.cell;

public interface CellLibrary {

    String getId();

    Cell getCellOrNull(String id);

    default Cell getCell(String id) throws NoSuchCellException {
        Cell cell = getCellOrNull(id);
        if (cell == null) {
            throw new NoSuchCellException(id);
        }
        return cell;
    }

    CellLibrary EMPTY = new CellLibrary() {

        @Override
        public String getId() {
            return "EMPTY";
        }

        @Override
        public Cell getCellOrNull(String id) {
            return null;
        }

    };

}
