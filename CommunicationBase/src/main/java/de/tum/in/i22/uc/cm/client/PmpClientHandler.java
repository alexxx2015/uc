package de.tum.in.i22.uc.cm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.processing.PmpProcessor;

/**
 * This class represents the client side of a remote {@link PmpProcessor}.
 *
 * This class operates on a {@link Connector} object that will be used to
 * connect to the remote {@link PmpProcessor}.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PmpClientHandler<HandleType> extends PmpProcessor implements IConnectable<HandleType> {
	protected static final Logger _logger = LoggerFactory.getLogger(PmpClientHandler.class);

	private final Connector<HandleType> _connector;

	protected HandleType _handle;

	/**
	 * Creates a new {@link PmpClientHandler} that will connect to a
	 * remote {@link PmpProcessor} by using the specified connector.
	 *
	 * @param connector the connector that will be used to connect to the remote {@link PmpProcessor}.
	 */
	public PmpClientHandler(Connector<HandleType> connector) {
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
