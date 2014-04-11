package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;



class ThriftPmp2PmpClient extends Pmp2PmpClient {
	private final ThriftConnector<TAny2Pmp.Client> _connector;

	private ThriftAny2PmpImpl _impl;

	private ThriftPmp2PmpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pmp.Client.class));
	}

	ThriftPmp2PmpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPmp2PmpClient(ThriftConnector<TAny2Pmp.Client> connector) {
		super(connector);
		_connector = connector;
	}


	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2PmpImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}


	@Override
	public IStatus receivePolicies(Set<String> policies) {
		return _impl.receivePolicies(policies);
	}

}
