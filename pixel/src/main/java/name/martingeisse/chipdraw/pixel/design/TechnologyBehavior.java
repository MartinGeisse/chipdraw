package name.martingeisse.chipdraw.pixel.design;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.drc.Drc;

/**
 *
 */
public interface TechnologyBehavior {

	/**
	 * Note: The plane schemas per group behave almost like a set, except that the "first" (topmost) plane schema is
	 * special. It is used to determine the currently visible plane group, and so it must be a plane that is not
	 * part of any "later" (lower) plane groups. We just use a list, but a set plus an extra field would be the
	 * more accurate solution.
	 */
	ImmutableList<ImmutableList<PlaneSchema>> getPlaneGroups();

	/**
	 * Gets the DRC for this technology.
	 */
	Drc getDrc();

	/**
	 * Default implementation that "does nothing".
	 */
	TechnologyBehavior DEFAULT = new TechnologyBehavior() {

		@Override
		public ImmutableList<ImmutableList<PlaneSchema>> getPlaneGroups() {
			return ImmutableList.of();
		}

		@Override
		public Drc getDrc() {
			return Drc.EMPTY_DRC;
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
		public ImmutableList<ImmutableList<PlaneSchema>> getPlaneGroups() {
			ImmutableList<ImmutableList<PlaneSchema>> planeGroups = wrapped.getPlaneGroups();
			if (planeGroups == null) {
				throw new ImplementationBug("getPlaneGroups() returned null");
			}
			for (ImmutableList<PlaneSchema> planeGroup : planeGroups) {
				if (planeGroup.isEmpty()) {
					throw new ImplementationBug("getPlaneGroups() result contains an empty group");
				}
			}
			return planeGroups;
		}

		@Override
		public Drc getDrc() {
			Drc drc = wrapped.getDrc();
			if (drc == null) {
				throw new ImplementationBug("getDrc() returned null");
			}
			return drc;
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
