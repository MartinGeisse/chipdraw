package name.martingeisse.chipdraw.unspice.netlist;

import com.google.common.collect.ImmutableList;

/**
 *
 */
public final class Fet implements Component {

	private final Dopant dopant;
	private final String drain;
	private final String gate;
	private final String source;
	private final String bulk;

	public Fet(Dopant dopant, String drain, String gate, String source, String bulk) {
		this.dopant = dopant;
		this.drain = drain;
		this.gate = gate;
		this.source = source;
		this.bulk = bulk;
	}

	public Dopant getDopant() {
		return dopant;
	}

	public String getDrain() {
		return drain;
	}

	public String getGate() {
		return gate;
	}

	public String getSource() {
		return source;
	}

	public String getBulk() {
		return bulk;
	}

	@Override
	public ImmutableList<String> getConnectedNets() {
		return ImmutableList.of(drain, gate, source, bulk);
	}

	@Override
	public void dump() {
		System.out.println(dopant.name() + "-FET: drain = " + drain + ", gate = " + gate + ", source = " + source + ", bulk = " + bulk);
	}

	public enum Dopant {
		N, P;
	}

}
