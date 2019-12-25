package name.martingeisse.chipdraw.pixel.tools;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class LibertyGeneratorMain {

    private static PrintWriter out;

    public static void main(String[] args) throws Exception {
        try (FileOutputStream fileOutputStream = new FileOutputStream("resource/7seg/own.lib")) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.US_ASCII)) {
                try (PrintWriter out = new PrintWriter(outputStreamWriter)) {
                    LibertyGeneratorMain.out = out;
                    out.println("");
                    out.println("/*");
                    out.println(" delay model :       typ");
                    out.println(" check model :       typ");
                    out.println(" power model :       typ");
                    out.println(" capacitance model : typ");
                    out.println(" other model :       typ");
                    out.println("*/");
                    out.println("library(osu05_stdcells) {");

                    printFunctionCell("Inverter", 28 * 70, "(!a)", "a");
                    printFunctionCell("Nand", 35 * 70, "(!(a b))", "a", "b");
                    printFunctionCell("Nand3", 42 * 70, "(!(a b c))", "a", "b", "c");
                    printFunctionCell("Nand4", 49 * 70, "(!(a b c d))", "a", "b", "c", "d");
                    printFunctionCell("Nor", 35 * 70, "(!(a+b))", "a", "b");
                    printFunctionCell("Nor3", 42 * 70, "(!(a+b+c))", "a", "b", "c");
                    printFunctionCell("Nor4", 49 * 70, "(!(a+b+c+d))", "a", "b", "c", "d");

                    printFunctionCell("Buffer", 35 * 70, "a", "a");
                    printFunctionCell("And", 49 * 70, "(a b)", "a", "b");
                    printFunctionCell("Or", 49 * 70, "(a+b)", "a", "b");

                    printFunctionCell("AndOrInvert", 42 * 70, "(!((a b)+c))", "a", "b", "c");
                    printFunctionCell("OrAndInvert", 42 * 70, "(!((a+b) c))", "a", "b", "c");
                    printFunctionCell("AndAndOrInvert", 49 * 70, "(!((a b)+(c d)))", "a", "b", "c", "d");
                    printFunctionCell("And3OrInvert", 49 * 70, "(!((a b c)+d))", "a", "b", "c", "d");

                    //
                    // "what-if" area: These cells do not yet exist, but we want to see what happens if they do.
                    //

                    // currently not useful
                    // printFunctionCell("And3And2OrInvert", 56 * 70, "(!((a b c)+(d e)))", "a", "b", "c", "d", "e");

                    // currently not useful
                    // printFunctionCell("Nand5", 56 * 70, "(!(a b c d e))", "a", "b", "c", "d", "e");

                    // currently not useful
                    // printFunctionCell("Nor5", 56 * 70, "(!(a+b+c+d+e))", "a", "b", "c", "d", "e");

                    // TODO useful
                    // printFunctionCell("SingleInverterNand", 42 * 70, "(!((!a) b))", "a", "b");

                    // currently not useful
                    // printFunctionCell("SingleInverterNand3", 49 * 70, "(!((!a) b c))", "a", "b", "c");

                    // TODO useful
                    // printFunctionCell("DoubleInverterNand3", 49 * 70, "(!((!a) (!b) c))", "a", "b", "c");

                    // currently not useful
                    // printFunctionCell("SingleInverterNor", 42 * 70, "(!((!a)+b))", "a", "b");

                    // currently not useful
                    // printFunctionCell("SingleInverterNor3", 42 * 70, "(!((!a)+b+c))", "a", "b", "c");

                    // currently not useful
                    // printFunctionCell("DoubleInverterNor3", 42 * 70, "(!((!a)+(!b)+c))", "a", "b", "c");

                    // TODO useful
                    // printFunctionCell("Xor", 56 * 70, "((a (!b))+((!a) b))", "a", "b");

                    // TODO useful
                    // printFunctionCell("Xnor", 56 * 70, "((a b)+((!a) (!b)))", "a", "b");

                    // TODO useful
                    // printFunctionCell("Mux", 56 * 70, "((s a)+((!s) b))", "s", "a", "b");

                    // ?
                    // printFunctionCell("And4OrInvert", 63 * 70, "(!((a b c d)+e))", "a", "b", "c", "d", "e");

                    // crashes abc and in turn yosys
                    // printFunctionCell("And3And2OrInvert", 63 * 70, "((a b c)+(d e))", "a", "b", "c", "d", "e");

                    // ?
                    // printFunctionCell("Or4AndInvert", 63 * 70, "(!((a+b+c+d) e))", "a", "b", "c", "d", "e");

                    // ?
                    // printFunctionCell("AndAndAndOr3Invert", 63 * 70, "(!((a b)+(c d)+(e f)))", "a", "b", "c", "d", "e", "f");

                    // ?
                    // printFunctionCell("OrAnd3Invert", 56 * 70, "(!((a+b) c d))", "a", "b", "c", "d");

                    // ?
                    // printFunctionCell("AndMux", 56 * 70, "((sx sy a)+(((!sx)+(!sy)) b))", "sx", "sy", "a", "b");

                    // ?
                    // printFunctionCell("OrMux", 56 * 70, "(((sx+sy) a)+((!sx) (!sy) b))", "sx", "sy", "a", "b");

                    // probably useful
                    // printFunctionCell("OrOrAnd3Invert", 56 * 70, "(!((a+b) (c+d) e))", "a", "b", "c", "d", "e");

                    // ?
                    // printFunctionCell("AndOr3Invert", 56 * 70, "(!((a b)+c+d))", "a", "b", "c", "d");

                    // ?
                    // printFunctionCell("AndAndOr3Invert", 63 * 70, "(!((a b)+(c d)+e))", "a", "b", "c", "d", "e");

                    out.println("}");
                }
            }
        }
    }

    private static void printFunctionCell(String name, int area, String function, String... inputPinNames) {
        beginCell(name, area);
        printInputPins(inputPinNames);
        printOutputPin("out", function);
        endCell();
    }

    private static void beginCell(String name, int area) {
        out.println("\tcell(" + name + ") {");
        out.println("\t\tarea: " + area + ";");
    }

    private static void printInputPins(String... names) {
        for (String name : names) {
            printInputPin(name);
        }
    }

    private static void printInputPin(String name) {
        out.println("\t\tpin(" + name + ") {");
        out.println("\t\t\tdirection: input;");
        out.println("\t\t}");
    }

    private static void printOutputPin(String name, String function) {
        out.println("\t\tpin(" + name + ") {");
        out.println("\t\t\tdirection: output;");
        out.println("\t\t\tfunction: \"" + function + "\";");
        out.println("\t\t}");
    }

    private static void endCell() {
        out.println("\t}");
    }

}
