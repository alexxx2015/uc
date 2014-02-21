package de.tum.in.i22.pip.core.eventdef;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class WriteFileEventHandler extends BaseEventHandler {

	private static final Logger _logger = Logger
			.getLogger(WriteFileEventHandler.class);

	public WriteFileEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
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
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		String processContainerId = instantiateProcess(pid, processName);

		InformationFlowModel ifModel = getInformationFlowModel();
		String fileContainerId = ifModel
				.getContainerIdByName(new Name(fileName));

		// check if container for filename exists and create new container if
		// not
		if (fileContainerId == null) {
			IContainer container = _messageFactory.createContainer();
			fileContainerId = ifModel.addContainer(container);
			IData data = _messageFactory.createData();
			String fileDataId = ifModel.addData(data);

			ifModel.addDataToContainerMapping(fileDataId, fileContainerId);

			ifModel.addName(new Name(fileName), fileContainerId);
		}

		ifModel.addDataToContainerMappings(
				ifModel.getDataInContainer(processContainerId), fileContainerId);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
