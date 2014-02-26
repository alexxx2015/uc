package de.tum.in.i22.pip.core.eventdef;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
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
		_logger.info("WriteFile event handler execute");

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

		IContainer processContainer = instantiateProcess(pid, processName);

		InformationFlowModel ifModel = getInformationFlowModel();
		IContainer fileContainer = ifModel
				.getContainer(new NameBasic(fileName));

		// check if container for filename exists and create new container if
		// not
		if (fileContainer == null) {
			fileContainer = _messageFactory.createContainer();
			ifModel.addContainer(fileContainer);
			IData data = _messageFactory.createData();
			ifModel.addData(data);

			ifModel.addDataToContainerMapping(data, fileContainer);

			ifModel.addName(new NameBasic(fileName), fileContainer);
		}

		ifModel.addDataToContainerMappings(
				ifModel.getDataInContainer(processContainer), fileContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
