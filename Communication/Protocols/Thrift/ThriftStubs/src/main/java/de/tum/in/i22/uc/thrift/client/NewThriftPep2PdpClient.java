package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.Any2PipClient;
import de.tum.in.i22.uc.cm.client.IConnectable;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.server.PipProcessor;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TPep2Pdp;


/**
 * The client side of a remote Thrift {@link PipProcessor} server.
 *
 * Create a instance of this class, connects it
 * and does calls on a remote {@link PipProcessor}.
 *
 * Use {@link ThriftClientFactory} to get an instance.
 *
 * @author Florian Kelbert
 *
 */
public class NewThriftPep2PdpClient implements IPep2Pdp, IConnectable {
	protected static final Logger _logger = LoggerFactory.getLogger(NewThriftPep2PdpClient.class);

	private TPep2Pdp.Client _handle;

	private final ThriftConnector<TPep2Pdp.Client> _connector;

	/**
	 * Creates a {@link NewThriftPep2PdpClient} that will be
	 * connected (upon calling {@link Any2PipClient#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	public NewThriftPep2PdpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TPep2Pdp.Client.class));
	}

	/**
	 * Creates a new {@link NewThriftPep2PdpClient} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param location the location of the remote point
	 */
	NewThriftPep2PdpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private NewThriftPep2PdpClient(ThriftConnector<TPep2Pdp.Client> connector) {
//		super(connector);
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
	public void notifyEventAsync(IEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		_logger.debug("notify event sync (Pdp client)");
		try {
			return ThriftConverter.fromThrift(
					_handle.
					notifyEventSync(
							ThriftConverter.
							toThrift(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


}
