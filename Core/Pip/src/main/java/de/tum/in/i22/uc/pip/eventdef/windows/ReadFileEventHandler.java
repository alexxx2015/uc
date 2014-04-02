package de.tum.in.i22.uc.pip.eventdef.windows;


import java.util.Set;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class ReadFileEventHandler extends BaseEventHandler {

	public ReadFileEventHandler() {
		super();
	}

	@Override
	protected IStatus execute() {
		String fileName = null;
		String pid = null;
		String processName = null;

		try {
			fileName = getParameterValue("InFileName");
			pid = getParameterValue("PID");
			processName = getParameterValue("ProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = WindowsEvents.instantiateProcess(pid, processName);

		IContainer fileContainer = basicIfModel.getContainer(new NameBasic(fileName));

		// check if container for filename exists and create new container
		if (fileContainer == null) {
			fileContainer = _messageFactory.createContainer();
			basicIfModel.addName(new NameBasic(fileName), fileContainer);
		}

		// add data to transitive reflexive closure of process container
		Set<IContainer> transitiveReflexiveClosure = basicIfModel.getAliasTransitiveReflexiveClosure(processContainer);
		Set<IData> dataSet = basicIfModel.getDataInContainer(fileContainer);
		for (IContainer tempContainer : transitiveReflexiveClosure) {
			basicIfModel.addDataToContainer(dataSet, tempContainer);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
