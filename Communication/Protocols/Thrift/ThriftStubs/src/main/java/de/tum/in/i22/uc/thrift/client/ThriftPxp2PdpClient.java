package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;

import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pxp2PdpClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;



class ThriftPxp2PdpClient extends Pxp2PdpClient {
	private final ThriftConnector<TAny2Pdp.Client> _connector;

	private ThriftAny2PdpImpl _impl;

	private ThriftPxp2PdpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pdp.Client.class));
	}

	ThriftPxp2PdpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPxp2PdpClient(ThriftConnector<TAny2Pdp.Client> connector) {
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
	public boolean registerPxp(PxpSpec pxp) {
		return _impl.registerPxp(pxp);
	}

}
