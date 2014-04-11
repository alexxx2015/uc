package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PxpClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pxp;



class ThriftPdp2PxpClient extends Pdp2PxpClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPdp2PxpClient.class);

	private final ThriftConnector<TAny2Pxp.Client> _connector;

	private ThriftAny2PxpImpl _impl;

	private ThriftPdp2PxpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pxp.Client.class));
	}

	ThriftPdp2PxpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPdp2PxpClient(ThriftConnector<TAny2Pxp.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2PxpImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public IStatus executeSync(List<IEvent> event) {
		return _impl.executeSync(event);
	}

	@Override
	public void executeAsync(List<IEvent> event) {
		_impl.executeAsync(event);
	}

}
