package name.martingeisse.chipdraw.pixel.tools;

public class LibertyGeneratorMain {

    public static void main(String[] args) {
        System.out.println("");
        System.out.println("/*");
        System.out.println(" delay model :       typ");
        System.out.println(" check model :       typ");
        System.out.println(" power model :       typ");
        System.out.println(" capacitance model : typ");
        System.out.println(" other model :       typ");
        System.out.println("*/");
        System.out.println("library(osu05_stdcells) {");

        printFunctionCell("Inverter", 28 * 70, "(!a)", "a");
        printFunctionCell("Nand", 35 * 70, "(!(a b))", "a", "b");
        printFunctionCell("Nand3", 35 * 70, "(!(a b c))", "a", "b", "c");
        printFunctionCell("Nor", 42 * 70, "(!(a+b))", "a", "b");
        printFunctionCell("Nor3", 42 * 70, "(!(a+b+c))", "a", "b", "c");

        printFunctionCell("Buffer", 35 * 70, "a", "a");
        printFunctionCell("And", 49 * 70, "(a b)", "a", "b");
        printFunctionCell("Or", 49 * 70, "(a+b)", "a", "b");

        printFunctionCell("AndOrInvert", 42 * 70, "(!((a b)+c))", "a", "b", "c");
        printFunctionCell("OrAndInvert", 42 * 70, "(!((a+b) c))", "a", "b", "c");
        printFunctionCell("AndAndOrInvert", 49 * 70, "(!((a b)+(c d)))", "a", "b", "c", "d");

        System.out.println("}");
    }

    private static void printFunctionCell(String name, int area, String function, String... inputPinNames) {
        beginCell(name, area);
        printInputPins(inputPinNames);
        printOutputPin("out", function);
        endCell();
    }

    private static void beginCell(String name, int area) {
        System.out.println("\tcell(" + name + ") {");
        System.out.println("\t\tarea: " + area + ";");
    }

    private static void printInputPins(String... names) {
        for (String name : names) {
            printInputPin(name);
        }
    }

    private static void printInputPin(String name) {
        System.out.println("\t\tpin(" + name + ") {");
        System.out.println("\t\t\tdirection: input;");
        System.out.println("\t\t}");
    }

    private static void printOutputPin(String name, String function) {
        System.out.println("\t\tpin(" + name + ") {");
        System.out.println("\t\t\tdirection: output;");
        System.out.println("\t\t\tfunction: \"" + function + "\";");
        System.out.println("\t\t}");
    }

    private static void endCell() {
        System.out.println("\t}");
    }

}
