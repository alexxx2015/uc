package de.tum.in.i22.uc.cm.client;

import java.util.Objects;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pxp;

/**
 * This class represents the client side of a remote Pxp
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PxpClientHandler implements IAny2Pxp, IConnectable {

	private final Connector<?> _connector;

	protected PxpClientHandler(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof PxpClientHandler) {
			return _connector.equals(((PxpClientHandler) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}
}
