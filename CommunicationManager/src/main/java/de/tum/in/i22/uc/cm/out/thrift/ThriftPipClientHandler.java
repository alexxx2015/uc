package de.tum.in.i22.uc.cm.out.thrift;

import java.io.File;
import java.util.Collection;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean evaluatePredicatCurrentState(String predicate) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<IContainer> getContainersForData(IData data) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IStatus notifyActualEvent(IEvent event) {
		try {
			return ThriftTypeConversion.convert(_handle.notifyActualEvent(ThriftTypeConversion.convert(event)));
		} catch (TException e) {
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public IStatus startSimulation() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IStatus stopSimulation() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isSimulating() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean hasAllData(Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean hasAnyData(Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean hasAllContainers(Collection<IContainer> container) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean hasAnyContainer(Collection<IContainer> container) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus initialRepresentation(IName container, Set<IData> data) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile,
			EConflictResolution flagForTheConflictResolution) {
		// TODO Auto-generated method stub
		return null;
	}


}
