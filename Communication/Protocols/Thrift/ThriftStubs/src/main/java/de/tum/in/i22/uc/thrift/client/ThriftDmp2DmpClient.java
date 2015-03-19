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
import de.tum.in.i22.uc.cm.distribution.client.Dmp2DmpClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Dmp;


class ThriftDmp2DmpClient extends Dmp2DmpClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftDmp2DmpClient.class);

	private final ThriftConnector<TAny2Dmp.Client> _connector;

	private ThriftAny2DmpImpl _impl;

	private ThriftDmp2DmpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Dmp.Client.class));
	}

	ThriftDmp2DmpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftDmp2DmpClient(ThriftConnector<TAny2Dmp.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2DmpImpl(_connector.connect());
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
