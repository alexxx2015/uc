package de.tum.in.i22.uc.thrift.client;

import java.io.File;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.server.PipProcessor;
import de.tum.in.i22.uc.thrift.ThriftConverter;


/**
 * The client side of a remote Thrift {@link PipProcessor} server.
 *
 * Create a instance of this class, connect it
 * (using {@link PipClientHandler#connect()}) and
 * do calls on a remote {@link PipProcessor}.
 *
 * Use {@link ThriftClientHandlerFactory} to get an instance.
 *
 * @author Florian Kelbert
 *
 */
class ThriftPipClientHandler extends PipClientHandler<TAny2Pip.Client> {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPipClientHandler.class);

	/**
	 * Creates a {@link ThriftPipClientHandler} that will be
	 * connected (upon calling {@link PipClientHandler#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientHandlerFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	ThriftPipClientHandler(String address, int port) {
		super(new ThriftConnector<>(address, port, TAny2Pip.Client.class));
	}

	/**
	 * Creates a new {@link ThriftPipClientHandler} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientHandlerFactory} to get an instance.
	 *
	 * @param location the location of the remote point
	 */
	ThriftPipClientHandler(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	@Override
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		try {
			return _handle.evaluatePredicateSimulatingNextState(ThriftConverter.toThrift(event), predicate);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Boolean evaluatePredicateCurrentState(String predicate) {
		try {
			return _handle.evaluatePredicatCurrentState(predicate);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Set<IContainer> getContainersForData(IData data) {
		try {
			return ThriftConverter.fromThriftContainerSet(_handle.getContainerForData(ThriftConverter.toThrift(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		try {
			return ThriftConverter.fromThriftDataSet(_handle.getDataInContainer(ThriftConverter.toThrift(container)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus notifyActualEvent(IEvent event) {
		try {
			return ThriftConverter.fromThrift(_handle.notifyActualEvent(ThriftConverter.toThrift(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus startSimulation() {
		try {
			return ThriftConverter.fromThrift(_handle.startSimulation());
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus stopSimulation() {
		try {
			return ThriftConverter.fromThrift(_handle.stopSimulation());
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean isSimulating() {
		try {
			return _handle.isSimulating();
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAllData(Set<IData> data) {
		try {
			return _handle.hasAllData(ThriftConverter.toThriftDataSet(data));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAnyData(Set<IData> data) {
		try {
			return _handle.hasAnyData(ThriftConverter.toThriftDataSet(data));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAllContainers(Set<IContainer> containers) {
		try {
			return _handle.hasAllContainers(ThriftConverter.toThriftContainerSet(containers));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAnyContainer(Set<IContainer> containers) {
		try {
			return _handle.hasAnyContainer(ThriftConverter.toThriftContainerSet(containers));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		try {
			return ThriftConverter.fromThrift(_handle.initialRepresentation(ThriftConverter.toThrift(containerName), ThriftConverter.toThriftDataSet(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile,
			EConflictResolution flagForTheConflictResolution) {
		// TODO Auto-generated method stub
		// not yet supported by thrift interface
		return null;
	}


}
