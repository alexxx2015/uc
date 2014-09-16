package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PepClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pep;

class ThriftPdp2PepClient extends Pdp2PepClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPdp2PepClient.class);

	private final ThriftConnector<TAny2Pep.Client> _connector;

	private ThriftAny2PepImpl _impl;

	private ThriftPdp2PepClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pep.Client.class));
	}

	ThriftPdp2PepClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPdp2PepClient(ThriftConnector<TAny2Pep.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2PepImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public IPLocation getResponsiblePdpLocation() {
		return _impl.getResponsiblePdpLocation();
	}


}
