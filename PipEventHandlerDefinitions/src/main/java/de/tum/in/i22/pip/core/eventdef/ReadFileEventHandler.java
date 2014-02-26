package de.tum.in.i22.pip.core.eventdef;


import java.util.Set;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ReadFileEventHandler extends BaseEventHandler {

	private static final Logger _logger = Logger
			.getLogger(ReadFileEventHandler.class);

	public ReadFileEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("ReadFile event handler execute");

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
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = instantiateProcess(pid, processName);

		InformationFlowModel ifModel = getInformationFlowModel();
		IContainer fileContainer = ifModel.getContainer(new NameBasic(fileName));

		// check if container for filename exists and create new container
		if (fileContainer == null) {
			fileContainer = _messageFactory.createContainer();
			ifModel.add(fileContainer);
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
