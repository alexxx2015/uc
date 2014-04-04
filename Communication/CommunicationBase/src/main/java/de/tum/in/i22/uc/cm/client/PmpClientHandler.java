package de.tum.in.i22.uc.cm.client;

import java.util.Objects;

import de.tum.in.i22.uc.cm.server.PmpProcessor;

/**
 * This class represents the client side of a remote {@link PmpProcessor}
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PmpClientHandler extends PmpProcessor implements IConnectable {

	private final Connector<?> _connector;

	protected PmpClientHandler(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof PmpClientHandler) {
			return _connector.equals(((PmpClientHandler) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}
}
