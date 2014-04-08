package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.Any2PxpClient;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pxp;


/**
 * The client side of a remote Thrift {@link PxpProcessor} server.
 *
 * Create a instance of this class, connect it
 * (using {@link Any2PxpClient#connect()}) and
 * do calls on a remote {@link PxpProcessor}.
 *
 * Use {@link ThriftClientFactory} to get an instance.
 *
 * @author Enrico  Lovat
 *
 */
class ThriftAny2PxpClient extends Any2PxpClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PxpClient.class);

	private TAny2Pxp.Client _handle;

	private final ThriftConnector<TAny2Pxp.Client> _connector;

	/**
	 * Creates a {@link ThriftAny2PxpClient} that will be
	 * connected (upon calling {@link Any2PxpClient#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	private ThriftAny2PxpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pxp.Client.class));
	}

	/**
	 * Creates a new {@link ThriftAny2PxpClient} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *--
	 * @param location the location of the remote point
	 */
	ThriftAny2PxpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftAny2PxpClient(ThriftConnector<TAny2Pxp.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
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
