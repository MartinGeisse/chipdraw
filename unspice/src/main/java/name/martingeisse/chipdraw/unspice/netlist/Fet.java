package name.martingeisse.chipdraw.unspice.netlist;

/**
 *
 */
public final class Fet implements Component {

	private final Dopant dopant;
	private final String terminal0;
	private final String terminal1;
	private final String terminal2;
	private final String terminal3;

	public Fet(Dopant dopant, String terminal0, String terminal1, String terminal2, String terminal3) {
		this.dopant = dopant;
		this.terminal0 = terminal0;
		this.terminal1 = terminal1;
		this.terminal2 = terminal2;
		this.terminal3 = terminal3;
	}

	public Dopant getDopant() {
		return dopant;
	}

	public String getTerminal0() {
		return terminal0;
	}

	public String getTerminal1() {
		return terminal1;
	}

	public String getTerminal2() {
		return terminal2;
	}

	public String getTerminal3() {
		return terminal3;
	}

	@Override
	public void dump() {
		System.out.println(dopant.name() + "-FET: " + terminal0 + ", " + terminal1 + ", " + terminal2 + ", " + terminal3);
	}

	public enum Dopant {
		N, P;
	}

}
