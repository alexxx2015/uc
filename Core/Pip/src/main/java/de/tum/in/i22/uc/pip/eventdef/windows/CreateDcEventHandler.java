package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 * Print event
 *
 * @author Stoimenov
 *
 */
public class CreateDcEventHandler extends BaseEventHandler {

	public CreateDcEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
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

		IContainer processContainer = WindowsEvents.instantiateProcess(pid, processName);

		IContainer deviceContainer = basicIfModel.getContainer(new NameBasic(
				deviceName));

		// check if container for device exists and create new container if not
		if (deviceContainer == null) {
			deviceContainer = _messageFactory.createContainer();
			basicIfModel.addName(new NameBasic(deviceName), deviceContainer);
		}

		basicIfModel.addData(
				basicIfModel.getData(processContainer),
				deviceContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
