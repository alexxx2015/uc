package de.tum.in.i22.uc.cm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.server.PipProcessor;

/**
 * This class represents the client side of a remote {@link PipProcessor}.
 *
 * This class operates on a {@link Connector} object that will be used to
 * connect to the remote {@link PipProcessor}.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PipClientHandler<HandleType> extends PipProcessor implements IConnectable<HandleType> {
	protected static final Logger _logger = LoggerFactory.getLogger(PipClientHandler.class);

	private final Connector<HandleType> _connector;

	protected HandleType _handle;

	/**
	 * Creates a new {@link PipClientHandler} that will connect to a
	 * remote {@link PipProcessor} by using the specified connector.
	 *
	 * @param connector the connector that will be used to connect to the remote {@link PipProcessor}.
	 */
	public PipClientHandler(Connector<HandleType> connector) {
		_connector = connector;
	}

	@Override
	public final HandleType connect() throws Exception {
		_handle = _connector.connect();
		return _handle;
	}

	@Override
	public final void disconnect() {
		_connector.disconnect();
		_handle = null;
	}
}
