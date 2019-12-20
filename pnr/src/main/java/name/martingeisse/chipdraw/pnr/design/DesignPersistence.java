package name.martingeisse.chipdraw.pnr.design;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pnr.cell.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class DesignPersistence {

    public static final String FORMAT_TOKEN_VERSION_0000 = "CDPR0000";

    private final CellLibraryRepository cellLibraryRepository;

    public DesignPersistence(CellLibraryRepository cellLibraryRepository) {
        this.cellLibraryRepository = cellLibraryRepository;
    }

    public void save(Design design, String path) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {
                try (PrintWriter out = new PrintWriter(outputStreamWriter)) {
                    out.println(FORMAT_TOKEN_VERSION_0000);
                    out.println(design.getWidth());
                    out.println(design.getHeight());
                    out.println(design.getCellLibrary().getId());
                    out.println("---");
                    for (RoutingPlane routingPlane : design.getRoutingPlanes()) {
                        for (int y = 0; y < design.getHeight(); y++) {
                            for (int x = 0; x < design.getWidth(); x++) {
                                out.print((char)('0' + routingPlane.getTile(x, y).ordinal()));
                            }
                            out.println();
                        }
                        out.println("---");
                    }
                    CellPlane cellPlane = design.getCellPlane();
                    for (CellInstance cellInstance : cellPlane.getCellInstances()) {
                        out.println(cellInstance.getTemplate().getId() + ' ' + cellInstance.getX() + ' ' + cellInstance.getY());
                    }
                    out.println("---");
                }
            }
        }
    }

    public Design load(String path) throws IOException, NoSuchCellLibraryException, NoSuchCellException {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {
                try (LineNumberReader in = new LineNumberReader(inputStreamReader)) {
                    String actualFormatToken = in.readLine();
                    if (!actualFormatToken.equals(FORMAT_TOKEN_VERSION_0000)) {
                        throw new IOException("wrong format token: " + actualFormatToken);
                    }
                    int width = expectIntLine(in);
                    int height = expectIntLine(in);
                    String cellLibraryId = expectLine(in);
                    CellLibrary cellLibrary = cellLibraryRepository.getCellLibrary(cellLibraryId);
                    expectSeparatorLine(in);
                    Design design = new Design(cellLibrary, width, height);
                    for (RoutingPlane routingPlane : design.getRoutingPlanes()) {
                        for (int y = 0; y < design.getHeight(); y++) {
                            String line = expectLine(in);
                            if (line.length() != width) {
                                throw new IOException("expected line width of " + width);
                            }
                            for (int x = 0; x < design.getWidth(); x++) {
                                char c = line.charAt(x);
                                if (c < '0' || c > '7') {
                                    throw new IOException("unexpected routing tile: " + c);
                                }
                                RoutingTile routingTile = RoutingTile.values()[c - '0'];
                                routingPlane.setTile(x, y, routingTile);
                            }
                        }
                        expectSeparatorLine(in);
                    }
                    CellPlane cellPlane = design.getCellPlane();
                    while (true) {
                        String line = expectLine(in);
                        if (line.equals("---")) {
                            break;
                        }
                        String[] segments = StringUtils.split(line);
                        if (segments.length != 3) {
                            throw new IOException("invalid cell line");
                        }
                        String cellTemplateId = segments[0];
                        CellTemplate cellTemplate = cellLibrary.getCellTemplate(cellTemplateId);
                        int x = Integer.parseInt(segments[1]);
                        int y = Integer.parseInt(segments[2]);
                        cellPlane.add(new CellInstance(cellTemplate, x, y));
                    }
                    expectEof(in);
                    return design;
                }
            }
        }
    }

    private static String expectLine(LineNumberReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("unexpected EOF");
        }
        return line;
    }

    private static int expectIntLine(LineNumberReader in) throws IOException {
        try {
            return Integer.parseInt(in.readLine());
        } catch (Exception e) {
            throw new IOException("expected separator line");
        }
    }

    private static void expectSeparatorLine(LineNumberReader in) throws IOException {
        if (!"---".equals(in.readLine())) {
            throw new IOException("expected separator line");
        }
    }

    private static void expectEof(LineNumberReader in) throws IOException {
        while (true) {
            String line = in.readLine();
            if (line == null) {
                return;
            }
            if (!line.isEmpty()) {
                throw new IOException("expected EOF, found: " + line);
            }
        }
    }

}
