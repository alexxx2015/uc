package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class WriteFileEventHandler extends WindowsEvents {

	public WriteFileEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
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

		IContainer fileContainer = _informationFlowModel
				.getContainer(new NameBasic(fileName));

		// check if container for filename exists and create new container if
		// not
		if (fileContainer == null) {
			fileContainer = _messageFactory.createContainer();
			IData data = _messageFactory.createData();

			_informationFlowModel.addData(data, fileContainer);

			_informationFlowModel.addName(new NameBasic(fileName), fileContainer);
		}

		_informationFlowModel.addData(
				_informationFlowModel.getData(processContainer), fileContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
