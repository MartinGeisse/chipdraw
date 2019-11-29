package name.martingeisse.chipdraw.unspice;

import name.martingeisse.chipdraw.unspice.input.SpiceLoader;
import name.martingeisse.chipdraw.unspice.mapper.Mapper;
import name.martingeisse.chipdraw.unspice.netlist.Netlist;

import java.io.File;

/**
 *
 */
public class Main {

	private static final String MAIN_SPICE_FILE = "resource/unspice/nand.spice";

	public static void main(String[] args) {
		File mainFile = new File(MAIN_SPICE_FILE);
		Netlist netlist = new SpiceLoader(mainFile).load();
		new Mapper(netlist).map();
		netlist.dump();
	}

}
