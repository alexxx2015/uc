package de.tum.in.i22.pip.core.eventdef;


import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.ContainerName;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class CreateWindowEventHandler extends BaseEventHandler {

	public CreateWindowEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
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

		String processContainerId = instantiateProcess(pid, processName);

		InformationFlowModel ifModel = getInformationFlowModel();
		String containerIdByWindowHandle = ifModel.getContainerIdByName(new ContainerName(windowHandle));

		// check if container for window exists and create new container if not
		if (containerIdByWindowHandle == null) {
			IContainer container = _messageFactory.createContainer();
			containerIdByWindowHandle = ifModel.addContainer(container);
			ifModel.addName(new ContainerName(windowHandle), containerIdByWindowHandle);
		}

		ifModel.addDataToContainerMappings(ifModel.getDataInContainer(processContainerId), containerIdByWindowHandle);
		ifModel.addAlias(processContainerId, containerIdByWindowHandle);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
