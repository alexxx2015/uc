package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;

import de.tum.in.i22.uc.cm.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;



class ThriftPep2PdpClient extends Pep2PdpClient {
	private final ThriftConnector<TAny2Pdp.Client> _connector;

	private ThriftAny2PdpImpl _impl;

	private ThriftPep2PdpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pdp.Client.class));
	}

	ThriftPep2PdpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPep2PdpClient(ThriftConnector<TAny2Pdp.Client> connector) {
		super(connector);
		_connector = connector;
	}


	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2PdpImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		_impl.notifyEventAsync(event);
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		return _impl.notifyEventSync(event);
	}


}
