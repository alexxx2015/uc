package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Distr2DistrClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Distr;


class ThriftDistr2DistrClient extends Distr2DistrClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftDistr2DistrClient.class);

	private final ThriftConnector<TAny2Distr.Client> _connector;

	private ThriftAny2DistrImpl _impl;

	private ThriftDistr2DistrClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Distr.Client.class));
	}

	ThriftDistr2DistrClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftDistr2DistrClient(ThriftConnector<TAny2Distr.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2DistrImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public IStatus remoteTransfer(Set<XmlPolicy> policies, String fromHost, IName containerName, Set<IData> data) {
		return _impl.remoteTransfer(policies, fromHost, containerName, data);
	}

}
