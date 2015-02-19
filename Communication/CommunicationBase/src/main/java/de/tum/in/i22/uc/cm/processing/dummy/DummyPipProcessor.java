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
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class DummyPipProcessor extends PipProcessor implements IDummyProcessor {
	public DummyPipProcessor() {
		super(LocalLocation.getInstance());
	}

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
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("initialRepresentation method invoked");
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

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("newStructuredData method invoked");
		return null;
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("getStructureOf method invoked");
		return null;
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("flattenStructure method invoked");
		return null;
	}

	@Override
	public IData getDataFromId(String id) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("getDataFromId method invoked");
		return null;
	}

	@Override
	public IStatus addJPIPListener(String ip, int port, String id, String filter) {
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("addJPIPListener method invoked");
		return null;
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("setUpdateFrequency method invoked");
		return null;
	}

	@Override
	public boolean newChecksum(IData data, IChecksum checksum, boolean overwrite) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("newChecksum method invoked");
		return false;
	}

	@Override
	public IChecksum getChecksumOf(IData data) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("getChecksumOf method invoked");
		return null;
	}

	@Override
	public boolean deleteChecksum(IData d) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("deleteChecksum method invoked");
		return false;
	}

	@Override
	public boolean deleteStructure(IData d) {
		// TODO Auto-generated method stub
		_logger.error("PipProcessor DUMMY Implementation");
		_logger.error("deleteStructure method invoked");
		return false;
	}

}
