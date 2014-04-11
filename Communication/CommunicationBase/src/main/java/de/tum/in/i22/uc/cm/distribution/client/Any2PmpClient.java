package de.tum.in.i22.uc.cm.distribution.client;

import java.util.Objects;

import de.tum.in.i22.uc.cm.processing.PmpProcessor;

/**
 * This class represents the client side of a remote {@link PmpProcessor}
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class Any2PmpClient extends PmpProcessor implements IConnectable {

	private final Connector<?> _connector;

	protected Any2PmpClient(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Any2PmpClient) {
			return _connector.equals(((Any2PmpClient) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}
}
