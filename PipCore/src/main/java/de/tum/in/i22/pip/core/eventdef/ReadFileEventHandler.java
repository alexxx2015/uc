package de.tum.in.i22.pip.core.eventdef;


import java.util.Set;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ReadFileEventHandler extends BaseEventHandler {

	public ReadFileEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
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

		IContainer processContainer = instantiateProcess(pid, processName);

		IContainer fileContainer = ifModel.getContainer(new NameBasic(fileName));

		// check if container for filename exists and create new container
		if (fileContainer == null) {
			fileContainer = _messageFactory.createContainer();
			ifModel.addName(new NameBasic(fileName), fileContainer);
		}

		// add data to transitive reflexive closure of process container
		Set<IContainer> transitiveReflexiveClosure = ifModel.getAliasTransitiveReflexiveClosure(processContainer);
		Set<IData> dataSet = ifModel.getDataInContainer(fileContainer);
		for (IContainer tempContainer : transitiveReflexiveClosure) {
			ifModel.addDataToContainerMappings(dataSet, tempContainer);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
