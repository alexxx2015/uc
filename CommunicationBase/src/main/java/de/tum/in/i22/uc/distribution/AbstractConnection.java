package de.tum.in.i22.uc.distribution;

public abstract class AbstractConnection implements IConnection {
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
