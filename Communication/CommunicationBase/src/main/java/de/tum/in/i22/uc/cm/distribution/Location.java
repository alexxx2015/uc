package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.distribution.IPLocation.ELocation;

public abstract class Location {
	private final ELocation _location;

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
