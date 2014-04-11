package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PipClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;



class ThriftPdp2PipClient extends Pdp2PipClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPdp2PipClient.class);

	private final ThriftConnector<TAny2Pip.Client> _connector;

	private ThriftAny2PipImpl _impl;

	private ThriftPdp2PipClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pip.Client.class));
	}

	ThriftPdp2PipClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPdp2PipClient(ThriftConnector<TAny2Pip.Client> connector) {
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
	public boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		return _impl.evaluatePredicateSimulatingNextState(event, predicate);
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		return _impl.evaluatePredicateCurrentState(predicate);
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
	public IStatus startSimulation() {
		return _impl.startSimulation();
	}

	@Override
	public IStatus stopSimulation() {
		return _impl.stopSimulation();
	}

	@Override
	public boolean isSimulating() {
		return _impl.isSimulating();
	}

	@Override
	public IStatus update(IEvent event) {
		return _impl.update(event);
	}
}
