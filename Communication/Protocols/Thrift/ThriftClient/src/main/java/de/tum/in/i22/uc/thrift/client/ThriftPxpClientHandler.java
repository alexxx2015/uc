package de.tum.in.i22.uc.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.PxpClientHandler;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pxp;


/**
 * The client side of a remote Thrift {@link PxpProcessor} server.
 *
 * Create a instance of this class, connect it
 * (using {@link PxpClientHandler#connect()}) and
 * do calls on a remote {@link PxpProcessor}.
 *
 * Use {@link ThriftClientHandlerFactory} to get an instance.
 *
 * @author Enrico  Lovat
 *
 */
class ThriftPxpClientHandler extends PxpClientHandler {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPxpClientHandler.class);

	private TAny2Pxp.Client _handle;

	private final ThriftConnector<TAny2Pxp.Client> _connector;

	/**
	 * Creates a {@link ThriftPxpClientHandler} that will be
	 * connected (upon calling {@link PxpClientHandler#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientHandlerFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	private ThriftPxpClientHandler(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pxp.Client.class));
	}

	/**
	 * Creates a new {@link ThriftPxpClientHandler} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientHandlerFactory} to get an instance.
	 *
	 * @param location the location of the remote point
	 */
	ThriftPxpClientHandler(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPxpClientHandler(ThriftConnector<TAny2Pxp.Client> connector) {
		super(connector);
		_connector = connector;
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

	@Override
	public IStatus executeSync(List<IEvent> event) {
		try {
			return ThriftConverter.fromThrift(
					_handle.executeSync(
							ThriftConverter.toThriftEventList(
									event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void executeAsync(List<IEvent> event) {
		try {
			_handle.executeAsync(ThriftConverter.toThriftEventList(event));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
