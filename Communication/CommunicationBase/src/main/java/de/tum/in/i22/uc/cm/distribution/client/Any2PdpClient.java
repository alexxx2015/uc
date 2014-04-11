package de.tum.in.i22.uc.cm.distribution.client;

import java.util.Objects;

import de.tum.in.i22.uc.cm.processing.PdpProcessor;

/**
 * This class represents the client side of a remote {@link PdpProcessor}
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class Any2PdpClient extends PdpProcessor implements IConnectable {

	private final Connector<?> _connector;

	protected Any2PdpClient(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Any2PdpClient) {
			return _connector.equals(((Any2PdpClient) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}
}
