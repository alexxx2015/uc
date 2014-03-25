package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class WriteFileEventHandler extends BaseEventHandler {

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

		IContainer processContainer = WindowsEvents.instantiateProcess(pid, processName);

		IContainer fileContainer = ifModel
				.getContainer(new NameBasic(fileName));

		// check if container for filename exists and create new container if
		// not
		if (fileContainer == null) {
			fileContainer = _messageFactory.createContainer();
			IData data = _messageFactory.createData();

			ifModel.addDataToContainer(data, fileContainer);

			ifModel.addName(new NameBasic(fileName), fileContainer);
		}

		ifModel.addDataToContainer(
				ifModel.getDataInContainer(processContainer), fileContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
