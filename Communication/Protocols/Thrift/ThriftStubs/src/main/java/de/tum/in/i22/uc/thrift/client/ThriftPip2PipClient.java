package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;


class ThriftPip2PipClient extends Pip2PipClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPip2PipClient.class);

	private final ThriftConnector<TAny2Pip.Client> _connector;

	private ThriftAny2PipImpl _impl;

	private ThriftPip2PipClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pip.Client.class));
	}

	ThriftPip2PipClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPip2PipClient(ThriftConnector<TAny2Pip.Client> connector) {
		super(connector);
		_connector = connector;
	}


	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2PipImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		return _impl.getContainersForData(data);
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		return _impl.getDataInContainer(container);
	}

	@Override
	public IStatus update(IEvent event) {
		return _impl.update(event);
	}

	@Override
	public boolean hasAllData(Set<IData> data) {
		return _impl.hasAllData(data);
	}

	@Override
	public boolean hasAnyData(Set<IData> data) {
		return _impl.hasAnyData(data);
	}

	@Override
	public boolean hasAllContainers(Set<IContainer> container) {
		return _impl.hasAllContainers(container);
	}

	@Override
	public boolean hasAnyContainer(Set<IContainer> container) {
		return _impl.hasAnyContainer(container);
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		return _impl.initialRepresentation(containerName, data);
	}
}
