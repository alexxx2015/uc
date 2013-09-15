package de.tum.in.i22.pip.core.actions;

import java.util.Set;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ReadFileActionHandler extends BaseActionHandler {

	private static final Logger _logger = Logger
			.getLogger(ReadFileActionHandler.class);

	public ReadFileActionHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("ReadFile action handler execute");
		
		String fileName = null;
		String pid = null;
		
		// currently not used
		String processName = null;
		
		try {
			fileName = getParameterValue("InFileName");
			pid = getParameterValue("PID");
			processName = getParameterValue("ProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		String processContainerId = instantiateProcess(pid, processName);

		InformationFlowModel ifModel = getInformationFlowModel();
		String fileContainerId = ifModel.getContainerIdByName(new Name(fileName));

		// check if container for filename exists and create new container
		if (fileContainerId == null) {
			fileContainerId = ifModel.addContainer(_messageFactory.createContainer());
			ifModel.addName(new Name(fileName), fileContainerId);
		}

		// add data to transitive reflexive closure of process container
		Set<String> transitiveReflexiveClosure = ifModel.getAliasTransitiveReflexiveClosure(processContainerId);
		Set<String> dataSet = ifModel.getDataInContainer(fileContainerId);
		for (String tempContainerID : transitiveReflexiveClosure) {
			ifModel.addDataToContainerMappings(dataSet, tempContainerID);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
