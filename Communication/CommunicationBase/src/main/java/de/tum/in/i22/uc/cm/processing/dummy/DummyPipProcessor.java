/**
 *
 *
 *  THIS
 *
 *  CLASS
 *
 *  IS
 *
 *  FOR
 *
 *  TESTING
 *
 *  PURPOSES
 *
 *  ONLY
 *
 *
 */

package de.tum.in.i22.uc.cm.processing.dummy;

import java.io.File;
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
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class DummyPipProcessor extends PipProcessor implements IDummyProcessor {
	private static Logger _logger = LoggerFactory.getLogger(DummyPipProcessor.class);

	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent event,
			String predicate) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("evaluatePredicateSimulatingNextState method invoked");
		return false;
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("evaluatePredicateCurrentState method invoked");
		return false;
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("getContainersForData method invoked");
		return null;
	}

	@Override
	public Set<IData> getDataInContainer(IName containerName) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("getDataInContainer method invoked");
		return null;
	}

	@Override
	public IStatus startSimulation() {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("startSimulation method invoked");
		return null;
	}

	@Override
	public IStatus stopSimulation() {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("stopSimulation method invoked");
		return null;
	}

	@Override
	public boolean isSimulating() {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("isSimulating method invoked");
		return false;
	}

	@Override
	public IStatus update(IEvent event) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("update method invoked");
		return null;
	}

	@Override
	public boolean hasAllData(Set<IData> data) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("hasAllData method invoked");
		return false;
	}

	@Override
	public boolean hasAnyData(Set<IData> data) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("hasAnyData method invoked");
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<IName> container) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("hasAllContainers method invoked");
		return false;
	}

	@Override
	public boolean hasAnyContainer(Set<IName> container) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("hasAnyContainer method invoked");
		return false;
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("initialRepresentation method invoked");
		return null;
	}

	@Override
	public Set<Location> whoHasData(Set<IData> data, int recursionDepth) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("whoHasData method invoked");
		return null;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution conflictResolutionFlag) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("updateInformationFlowSemantics method invoked");
		return null;
	}

	@Override
	public IData newInitialRepresentation(IName containerName) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("newInitialRepresentation method invoked");
		return null;
	}



}
