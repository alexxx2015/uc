package de.tum.in.i22.uc.distribution;

import java.util.Objects;

import de.tum.in.i22.uc.distribution.IPLocation.ELocation;

public class LocalLocation extends Location {
	public LocalLocation() {
		super(ELocation.LOCAL);
	}

	@Override
	public String toString() {
		return "local";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LocalLocation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getClass());
	}
}
