package de.tum.in.i22.uc.distribution;

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
