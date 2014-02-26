package de.tum.in.i22.pip.core.eventdef;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Print event
 * 
 * @author Stoimenov
 * 
 */
public class CreateDCEventHandler extends BaseEventHandler {

	private static final Logger _logger = Logger
			.getLogger(CreateDcEventHandler.class);

	public CreateDCEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("CreateDC event handler execute");
		String pid = null;
		String processName = null;
		String deviceName = null;
		try {
			pid = getParameterValue("PID");
			processName = getParameterValue("ProcessName");
			deviceName = getParameterValue("lpszDevice");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		String processContainerId = instantiateProcess(pid, processName);

		InformationFlowModel ifModel = getInformationFlowModel();
		String deviceContainerId = ifModel.getContainerIdByName(new NameBasic(
				deviceName));

		// check if container for device exists and create new container if not
		if (deviceContainerId == null) {
			IContainer container = _messageFactory.createContainer();
			deviceContainerId = ifModel.addContainer(container);
			ifModel.addName(new NameBasic(deviceName), deviceContainerId);
		}

		ifModel.addDataToContainerMappings(
				ifModel.getDataInContainer(processContainerId),
				deviceContainerId);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
