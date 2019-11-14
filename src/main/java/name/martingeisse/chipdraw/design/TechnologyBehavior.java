package name.martingeisse.chipdraw.design;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 *
 */
public interface TechnologyBehavior {

	void bind(Technology technology);
	ImmutableList<ImmutableSet<PlaneSchema>> getPlaneGroups();

	TechnologyBehavior DEFAULT = new TechnologyBehavior() {

		@Override
		public void bind(Technology technology) {
		}

		@Override
		public ImmutableList<ImmutableSet<PlaneSchema>> getPlaneGroups() {
			return ImmutableList.of();
		}

	};

	class SafeWrapper implements TechnologyBehavior {

		private final TechnologyBehavior wrapped;

		public SafeWrapper(TechnologyBehavior wrapped) {
			this.wrapped = wrapped;
		}

		public TechnologyBehavior getWrapped() {
			return wrapped;
		}

		@Override
		public void bind(Technology technology) {
			wrapped.bind(technology);
		}

		@Override
		public ImmutableList<ImmutableSet<PlaneSchema>> getPlaneGroups() {
			ImmutableList<ImmutableSet<PlaneSchema>> planeGroups = wrapped.getPlaneGroups();
			if (planeGroups == null) {
				throw new ImplementationBug("getPlaneGroups() returned null");
			}
			for (ImmutableSet<PlaneSchema> planeGroup : planeGroups) {
				if (planeGroup.isEmpty()) {
					throw new ImplementationBug("getPlaneGroups() result contains an empty group");
				}
			}
			return planeGroups;
		}

	}

	class ImplementationBug extends RuntimeException {

		public ImplementationBug() {
		}

		public ImplementationBug(String message) {
			super(message);
		}

		public ImplementationBug(String message, Throwable cause) {
			super(message, cause);
		}

		public ImplementationBug(Throwable cause) {
			super(cause);
		}

	}

}
