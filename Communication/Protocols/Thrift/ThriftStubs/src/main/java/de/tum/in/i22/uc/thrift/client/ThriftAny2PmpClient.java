package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.server.PmpProcessor;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TStatus;


/**
 * The client side of a remote Thrift {@link PmpProcessor} server.
 *
 * Create a instance of this class, connect it
 * (using {@link Any2PmpClient#connect()}) and
 * do calls on a remote {@link PmpProcessor}.
 *
 * Use {@link ThriftClientFactory} to get an instance.
 *
 * @author Florian Kelbert
 *
 */
class ThriftAny2PmpClient extends Any2PmpClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PmpClient.class);

	private TAny2Pmp.Client _handle;

	private final ThriftConnector<TAny2Pmp.Client> _connector;

	/**
	 * Creates a {@link ThriftAny2PmpClient} that will be
	 * connected (upon calling {@link Any2PmpClient#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	private ThriftAny2PmpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pmp.Client.class));
	}

	/**
	 * Creates a new {@link ThriftAny2PmpClient} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param location the location of the remote point
	 */
	ThriftAny2PmpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftAny2PmpClient(ThriftConnector<TAny2Pmp.Client> connector) {
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
	public IStatus receivePolicies(Set<String> policies) {
		try {
			TStatus status = _handle.remotePolicyTransfer(policies);
			return ThriftConverter.fromThrift(status);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus informRemoteDataFlow(Location location, Set<IData> data) {
		// TODO Auto-generated method stub
		return null;
	}

}
