package de.tum.in.i22.uc.thrift.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.PmpClientHandler;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.server.PmpProcessor;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;


/**
 * The client side of a remote Thrift {@link PmpProcessor} server.
 *
 * Create a instance of this class, connect it
 * (using {@link PmpClientHandler#connect()}) and
 * do calls on a remote {@link PmpProcessor}.
 *
 * Use {@link ThriftClientHandlerFactory} to get an instance.
 *
 * @author Florian Kelbert
 *
 */
class ThriftPmpClientHandler extends PmpClientHandler {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPmpClientHandler.class);

	private TAny2Pmp.Client _handle;

	private final ThriftConnector<TAny2Pmp.Client> _connector;

	/**
	 * Creates a {@link ThriftPmpClientHandler} that will be
	 * connected (upon calling {@link PmpClientHandler#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientHandlerFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	ThriftPmpClientHandler(String address, int port) {
		_connector = new ThriftConnector<>(address, port, TAny2Pmp.Client.class);
	}

	/**
	 * Creates a new {@link ThriftPmpClientHandler} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientHandlerFactory} to get an instance.
	 *
	 * @param location the location of the remote point
	 */
	ThriftPmpClientHandler(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	@Override
	public void connect() throws Exception {
		_handle = _connector.connect();
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_handle = null;

	}

}
