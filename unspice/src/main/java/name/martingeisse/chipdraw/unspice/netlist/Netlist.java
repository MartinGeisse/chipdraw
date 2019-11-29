package name.martingeisse.chipdraw.unspice.netlist;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class Netlist {

	private final Set<Component> components = new HashSet<>();

	public void add(Component component) {
		components.add(component);
	}

	public void dump() {
		for (Component component : components) {
			component.dump();
		}
	}

}
