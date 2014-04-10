package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.distribution.IPLocation.ELocation;

public abstract class Location implements IName {
	private final ELocation _location;

	protected static final String PREFIX_LOCATION = " LOC_";

	public Location(ELocation location) {
		_location = location;
	}

	@Override
	public abstract String toString();

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

	public ELocation getLocation() {
		return _location;
	}
}
