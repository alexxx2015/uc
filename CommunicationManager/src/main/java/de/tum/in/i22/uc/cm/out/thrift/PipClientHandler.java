package de.tum.in.i22.uc.cm.out.thrift;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pip.PipProcessor;

/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class PipClientHandler<Handle> extends PipProcessor implements ThriftClient {
	protected static final Logger _logger = LoggerFactory.getLogger(PipClientHandler.class);

	private final Connector<Handle> _connector;

	protected Handle _handle;

	public PipClientHandler(Connector<Handle> connector) {
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
