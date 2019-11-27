package name.martingeisse.chipdraw.pnr.cell;

public interface CellLibrary {

    String getId();

    CellTemplate getCellTemplateOrNull(String id);

    default CellTemplate getCellTemplate(String id) throws NoSuchCellException {
        CellTemplate cellTemplate = getCellTemplateOrNull(id);
        if (cellTemplate == null) {
            throw new NoSuchCellException(id);
        }
        return cellTemplate;
    }

    CellLibrary EMPTY = new CellLibrary() {

        @Override
        public String getId() {
            return "EMPTY";
        }

        @Override
        public CellTemplate getCellTemplateOrNull(String id) {
            return null;
        }

    };

}
