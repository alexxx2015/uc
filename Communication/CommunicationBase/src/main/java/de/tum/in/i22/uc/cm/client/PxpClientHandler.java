package de.tum.in.i22.uc.cm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pxp;

/**
 * This class represents the client side of a remote {@link PxpProcessor}.
 *
 * This class operates on a {@link Connector} object that will be used to
 * connect to the remote {@link PxpProcessor}.
 *
 * @author Enrico Lovat
 *
 */
public abstract class PxpClientHandler<HandleType> implements IConnectable<HandleType>, IAny2Pxp {
	protected static final Logger _logger = LoggerFactory.getLogger(PxpClientHandler.class);

	private final Connector<HandleType> _connector;

	protected HandleType _handle;

	/**
	 * Creates a new {@link PxpClientHandler} that will connect to a
	 * remote {@link PxpProcessor} by using the specified connector.
	 *
	 * @param connector the connector that will be used to connect to the remote {@link PxpProcessor}.
	 */
	public PxpClientHandler(Connector<HandleType> connector) {
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
