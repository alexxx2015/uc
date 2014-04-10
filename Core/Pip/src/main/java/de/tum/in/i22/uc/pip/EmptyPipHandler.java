package de.tum.in.i22.uc.pip;

import java.io.File;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class EmptyPipHandler extends PipProcessor {

	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public IStatus startSimulation() {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public IStatus stopSimulation() {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public boolean isSimulating() {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public IStatus update(IEvent event) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile,
			EConflictResolution flagForTheConflictResolution) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public boolean hasAllData(Set<IData> data) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public boolean hasAnyData(Set<IData> data) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public boolean hasAllContainers(Set<IName> container) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public boolean hasAnyContainer(Set<IName> container) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		throw new UnsupportedOperationException("No PipHandler deployed.");
	}
}
