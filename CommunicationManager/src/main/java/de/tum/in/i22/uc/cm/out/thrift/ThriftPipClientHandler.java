package de.tum.in.i22.uc.cm.out.thrift;

import java.io.File;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.TAny2Pip;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.thrift.ThriftTypeConversion;
import de.tum.in.i22.uc.pip.PipClientHandler;


/**
 *
 * @author Florian Kelbert
 *
 */
public class ThriftPipClientHandler extends PipClientHandler<TAny2Pip.Client> {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPipClientHandler.class);

	public ThriftPipClientHandler(String address, int port) {
		super(new ThriftConnector<>(address, port, TAny2Pip.Client.class));

		// TODO: Connection is never closed

		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		try {
			return _handle.evaluatePredicateSimulatingNextState(ThriftTypeConversion.toThrift(event), predicate);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Boolean evaluatePredicatCurrentState(String predicate) {
		try {
			return _handle.evaluatePredicatCurrentState(predicate);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Set<IContainer> getContainersForData(IData data) {
		try {
			return ThriftTypeConversion.fromThriftContainerSet(_handle.getContainerForData(ThriftTypeConversion.toThrift(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		try {
			return ThriftTypeConversion.fromThriftDataSet(_handle.getDataInContainer(ThriftTypeConversion.toThrift(container)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus notifyActualEvent(IEvent event) {
		try {
			return ThriftTypeConversion.fromThrift(_handle.notifyActualEvent(ThriftTypeConversion.toThrift(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus startSimulation() {
		try {
			return ThriftTypeConversion.fromThrift(_handle.startSimulation());
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus stopSimulation() {
		try {
			return ThriftTypeConversion.fromThrift(_handle.stopSimulation());
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
			return _handle.hasAllData(ThriftTypeConversion.toThriftDataSet(data));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAnyData(Set<IData> data) {
		try {
			return _handle.hasAnyData(ThriftTypeConversion.toThriftDataSet(data));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAllContainers(Set<IContainer> containers) {
		try {
			return _handle.hasAllContainers(ThriftTypeConversion.toThriftContainerSet(containers));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAnyContainer(Set<IContainer> containers) {
		try {
			return _handle.hasAnyContainer(ThriftTypeConversion.toThriftContainerSet(containers));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		try {
			return ThriftTypeConversion.fromThrift(_handle.initialRepresentation(ThriftTypeConversion.toThrift(containerName), ThriftTypeConversion.toThriftDataSet(data)));
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
