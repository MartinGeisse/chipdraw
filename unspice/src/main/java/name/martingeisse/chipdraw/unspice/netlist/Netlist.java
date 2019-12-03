package name.martingeisse.chipdraw.unspice.netlist;

import com.google.common.collect.ImmutableSet;

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

	public ImmutableSet<Component> getComponents() {
		return ImmutableSet.copyOf(components);
	}

	public ImmutableSet<String> getNets() {
		Set<String> nets = new HashSet<>();
		for (Component component : components) {
			nets.addAll(component.getConnectedNets());
		}
		return ImmutableSet.copyOf(nets);
	}

	public ImmutableSet<Component> getComponentsForNet(String net) {
		Set<Component> result = new HashSet<>();
		for (Component component : components) {
			if (component.getConnectedNets().contains(net)) {
				result.add(component);
			}
		}
		return ImmutableSet.copyOf(result);
	}

	public void dump() {
		for (Component component : components) {
			component.dump();
		}
	}

}
