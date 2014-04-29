package de.tum.in.i22.uc.pip.eventdef.windows;


import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class ReadFileEventHandler extends WindowsEvents {

	public ReadFileEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
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

		IContainer fileContainer = _informationFlowModel.getContainer(new NameBasic(fileName));

		// check if container for filename exists and create new container
		if (fileContainer == null) {
			fileContainer = _messageFactory.createContainer();
			_informationFlowModel.addName(new NameBasic(fileName), fileContainer);
		}

		// add data to transitive reflexive closure of process container
		Set<IContainer> transitiveReflexiveClosure = _informationFlowModel.getAliasTransitiveReflexiveClosure(processContainer);
		Set<IData> dataSet = _informationFlowModel.getData(fileContainer);
		for (IContainer tempContainer : transitiveReflexiveClosure) {
			_informationFlowModel.addData(dataSet, tempContainer);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
