package name.martingeisse.chipdraw.pnr.cell;

import com.google.common.collect.ImmutableList;

public interface CellLibrary {

    ImmutableList<String> getAllIds();

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
        public ImmutableList<String> getAllIds() {
            return ImmutableList.of();
        }

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
