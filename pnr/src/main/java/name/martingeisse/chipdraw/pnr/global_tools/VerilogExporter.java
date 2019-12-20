package name.martingeisse.chipdraw.pnr.global_tools;

import name.martingeisse.chipdraw.pnr.design.Design;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
                    out.println("module exported();");
                }
            }
        }
    }

}
