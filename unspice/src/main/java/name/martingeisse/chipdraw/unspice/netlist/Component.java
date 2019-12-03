package name.martingeisse.chipdraw.unspice.netlist;

import com.google.common.collect.ImmutableList;

/**
 *
 */
public interface Component {

	ImmutableList<String> getConnectedNets();

	void dump();

}
