package de.tum.in.i22.uc.cm.distribution;


public class LocalLocation extends Location {

	private static final String local = "local";

	public LocalLocation() {
		super(ELocation.LOCAL);
	}

	@Override
	public String toString() {
		return local;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LocalLocation;
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

	@Override
	public String getName() {
		return PREFIX_LOCATION + local;
	}

	@Override
	public String asString() {
		return local;
	}
}
