package de.tum.in.i22.uc.cm.distribution.client;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public abstract class GenericX2YClient implements IConnectable {

	private final Connector<?> _connector;

	protected GenericX2YClient(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof GenericX2YClient) {
			return _connector.equals(((GenericX2YClient) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_connector", _connector)
				.toString();
	}
}
