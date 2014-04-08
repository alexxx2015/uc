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
public abstract class Any2PxpClient implements IAny2Pxp, IConnectable {

	private final Connector<?> _connector;

	protected Any2PxpClient(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Any2PxpClient) {
			return _connector.equals(((Any2PxpClient) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}
}
