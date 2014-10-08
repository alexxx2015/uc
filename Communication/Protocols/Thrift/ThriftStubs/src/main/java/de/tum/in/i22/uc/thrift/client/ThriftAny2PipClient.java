package de.tum.in.i22.uc.thrift.client;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PipClient;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;


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
class ThriftAny2PipClient extends Any2PipClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PipClient.class);

	private ThriftAny2PipImpl _impl;

	private final ThriftConnector<TAny2Pip.Client> _connector;

	/**
	 * Creates a {@link ThriftAny2PipClient} that will be
	 * connected (upon calling {@link Any2PipClient#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	private ThriftAny2PipClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pip.Client.class));
	}

	/**
	 * Creates a new {@link ThriftAny2PipClient} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param location the location of the remote point
	 */
	ThriftAny2PipClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftAny2PipClient(ThriftConnector<TAny2Pip.Client> connector) {
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
	public Set<IData> getDataInContainer(IName containerName) {
		return _impl.getDataInContainer(containerName);
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

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile,
			EConflictResolution flagForTheConflictResolution) {
		return _impl.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		return _impl.initialRepresentation(containerName, data);
	}

	@Override
	public IData newInitialRepresentation(IName containerName) {
		return _impl.newInitialRepresentation(containerName);
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		return _impl.newStructuredData(structure);
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		return _impl.getStructureOf(data);
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		return _impl.flattenStructure(data);
	}

	@Override
	public IData getDataFromId(String id) {
		return _impl.getDataFromId(id);
	}

	@Override
	public IStatus addListener(String ip, int port, String id, String filter) {
		return _impl.addListener(ip, port, id, filter);
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		return _impl.setUpdateFrequency(msec, id);
	}
}
