package de.tum.in.i22.pip.core.eventdef;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class CreateWindowEventHandler extends BaseEventHandler {

	private static final Logger _logger = Logger
			.getLogger(CreateWindowEventHandler.class);

	public CreateWindowEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("CreateWindow event handler execute");

		String pid = null;
		String processName = null;
		String windowHandle = null;
		try {
			pid = getParameterValue("PID");
			processName = getParameterValue("ProcessName");
			windowHandle = getParameterValue("WindowHandle");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = instantiateProcess(pid, processName);

		InformationFlowModel ifModel = getInformationFlowModel();
		IContainer containerIdByWindowHandle = ifModel.getContainer(new NameBasic(windowHandle));

		// check if container for window exists and create new container if not
		if (containerIdByWindowHandle == null) {
			containerIdByWindowHandle = _messageFactory.createContainer();
			ifModel.addName(new NameBasic(windowHandle), containerIdByWindowHandle);
		}

		ifModel.addDataToContainerMappings(ifModel.getDataInContainer(processContainer), containerIdByWindowHandle);
		ifModel.addAlias(processContainer, containerIdByWindowHandle);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
