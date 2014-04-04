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
public abstract class PipClientHandler extends PipProcessor implements IConnectable {

	private final Connector<?> _connector;

	protected PipClientHandler(Connector<?> connector) {
		_connector = connector;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof PipClientHandler) {
			return _connector.equals(((PipClientHandler) obj)._connector);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_connector);
	}
}
