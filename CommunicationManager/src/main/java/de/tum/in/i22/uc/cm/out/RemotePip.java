package de.tum.in.i22.uc.cm.out;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import de.tum.i22.in.uc.cm.thrift.TAny2Pip;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.requests.GenericPipHandler;
import de.tum.in.i22.uc.distribution.IPLocation;

/**
 *
 * @author Florian Kelbert
 *
 */
public class RemotePip extends GenericPipHandler {
	private final TAny2Pip.Client _remotePip;

	public RemotePip(IPLocation location) {
		TTransport transportPip = new TSocket(location.getHost(), location.getPort());
		try {
			transportPip.open();
		} catch (TTransportException e) {
			throw new RuntimeException("Specified Pip [" + location + "] is unreachable");
		}

		_remotePip = new TAny2Pip.Client(new TBinaryProtocol(transportPip));
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
	public Set<IContainer> getContainerForData(IData data) {
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
		// TODO Auto-generated method stub
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
	public IStatus notifyDataTransfer(IName containerName, Collection<IData> data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus initialRepresentation(IContainer container, IData data) {
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
