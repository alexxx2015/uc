package de.tum.in.i22.uc.cm.client;

import java.util.Objects;

import de.tum.in.i22.uc.cm.server.PdpProcessor;

/**
 * This class represents the client side of a remote {@link PdpProcessor}
 * and is thus {@link IConnectable} to that remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PdpClientHandler extends PdpProcessor implements IConnectable {

	private final Connector<?> _connector;

	protected PdpClientHandler(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof PdpClientHandler) {
			return _connector.equals(((PdpClientHandler) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}
}
