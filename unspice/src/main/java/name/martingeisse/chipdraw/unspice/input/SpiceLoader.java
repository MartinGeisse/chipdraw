package name.martingeisse.chipdraw.unspice.input;

import name.martingeisse.chipdraw.unspice.netlist.Fet;
import name.martingeisse.chipdraw.unspice.netlist.Netlist;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public final class SpiceLoader {

	private final File mainFile;
	private Netlist netlist;

	public SpiceLoader(File mainFile) {
		this.mainFile = mainFile;
	}

	public Netlist load() throws IOException {
		netlist = new Netlist();
		try (SpiceLineReader lineReader = new SpiceLineReader(mainFile)) {
			while (true) {
				String line = lineReader.readLine();
				if (line == null) {
					break;
				}
				consumeLine(line.trim());
			}
		}
		return netlist;
	}

	private void consumeLine(String line) {

		// ignore empty lines
		if (line.isEmpty()) {
			return;
		}

		// ignore comments
		if (line.startsWith("*")) {
			return;
		}

		// ignore scale specifications
		if (line.startsWith(".option scale=")) {
			return;
		}

		// ignore capacitance for now
		if (line.startsWith("C")) {
			return;
		}

		// transistors
		if (line.startsWith("M")) {
			consumeTransistor(line);
			return;
		}

		// throw an error for anything we don't understand
		throw new RuntimeException("line not recognized: " + line);

	}

	private void consumeTransistor(String line) {
		String[] segments = StringUtils.split(line);
		if (segments.length != 12) {
			throw new RuntimeException("unexpected number of line segments for transistor: " + segments.length + " / " + StringUtils.join(segments, ' '));
		}
		Fet.Dopant dopant;
		if (segments[5].equals("nfet")) {
			dopant = Fet.Dopant.N;
		} else if (segments[5].equals("pfet")) {
			dopant = Fet.Dopant.P;
		} else {
			throw new RuntimeException("could not recognize dopant: " + segments[6]);
		}
		netlist.add(new Fet(dopant, segments[1], segments[2], segments[3], segments[4]));
	}

}
