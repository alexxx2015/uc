package de.tum.in.i22.uc.cm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.processing.PmpProcessor;

/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class PmpClientHandler<Handle> extends PmpProcessor implements IConnectable {
	protected static final Logger _logger = LoggerFactory.getLogger(PmpClientHandler.class);

	private final Connector<Handle> _connector;

	protected Handle _handle;

	public PmpClientHandler(Connector<Handle> connector) {
		_connector = connector;
	}

	@Override
	public final void connect() throws Exception {
		_handle = _connector.connect();
	}

	@Override
	public final void disconnect() {
		_connector.disconnect();
		_handle = null;
	}
}
