package de.tum.in.i22.uc.cm.client;

import java.util.Objects;

import de.tum.in.i22.uc.cm.server.PipProcessor;

/**
 * This class represents the client side of a remote {@link PipProcessor}
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class Any2PipClient extends PipProcessor implements IConnectable {

	private final Connector<?> _connector;

	protected Any2PipClient(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Any2PipClient) {
			return _connector.equals(((Any2PipClient) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}
}
