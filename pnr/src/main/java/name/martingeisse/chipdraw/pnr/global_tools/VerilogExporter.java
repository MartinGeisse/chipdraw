package name.martingeisse.chipdraw.pnr.global_tools;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pnr.cell.Port;
import name.martingeisse.chipdraw.pnr.design.CellInstance;
import name.martingeisse.chipdraw.pnr.design.Design;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VerilogExporter {

    private final Design design;
    private final File file;

    public VerilogExporter(Design design, File file) {
        this.design = design;
        this.file = file;
    }

    public void export() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.US_ASCII)) {
                try (PrintWriter out = new PrintWriter(outputStreamWriter)) {
                    export(out);
                }
            }
        }
    }

    private void export(PrintWriter out) {
        out.println("module exported();");
        List<Net> nets = new ArrayList<>();
        // TODO recognize pad nets by a "mesh" of a certain minimum size in routing layer 0.
        // Will not assign nice names to pads but we can accept that.
        new ConnectivityExtractor3d() {

            private String netName;
            private List<PortConnection> portConnections;

            @Override
            protected void beginPatch(int planeIndex, int x, int y) {
                netName = "net_" + planeIndex + "_" + x + "_" + y;
                portConnections = new ArrayList<>();
            }

            @Override
            protected void handleCellContact(int x, int y) {
                Pair<CellInstance, Port> instanceAndPort = design.getCellPlane().findInstanceAndPortForPosition(x, y);
                if (instanceAndPort == null) {
                    throw new RuntimeException("did not find cell port at " + x + ", " + y);
                }
                portConnections.add(new PortConnection(instanceAndPort.getLeft(), instanceAndPort.getRight()));
            }

            @Override
            protected void finishPatch() {
                nets.add(new Net(netName, ImmutableList.copyOf(portConnections)));
            }

        }.extract(design);
        for (Net net : nets) {
            out.println("\twire " + net.getName() + ";");
        }
        for (CellInstance cellInstance : design.getCellPlane().getCellInstances()) {
            out.print("\t" + cellInstance.getTemplate().getId() +
                    " cell_" + cellInstance.getX() + "_" + cellInstance.getY());
            // TODO
            out.println("\t);");
        }
        out.println("endmodule");
    }

    public static final class Net {

        private final String name;
        private final ImmutableList<PortConnection> portConnections;

        public Net(String name, ImmutableList<PortConnection> portConnections) {
            this.name = name;
            this.portConnections = portConnections;
        }

        public String getName() {
            return name;
        }

        public ImmutableList<PortConnection> getPortConnections() {
            return portConnections;
        }

    }

    public static final class PortConnection {

        private final CellInstance cellInstance;
        private final Port port;

        public PortConnection(CellInstance cellInstance, Port port) {
            this.cellInstance = cellInstance;
            this.port = port;
        }

        public CellInstance getCellInstance() {
            return cellInstance;
        }

        public Port getPort() {
            return port;
        }

    }

}
