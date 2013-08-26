package de.tum.in.i22.pip.core.actions;

import java.util.Set;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.PipName;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ReadFileActionHandler extends BaseActionHandler {
	
	private final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();

	private static final Logger _logger = Logger
			.getLogger(ReadFileActionHandler.class);

	public ReadFileActionHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("ReadFile action handler execute");

		InformationFlowModel ifModel = getInformationFlowModel();
		String fileName = null;
		String pid = null;
		String processName = null;
		
		try {
			fileName = getParameterValue("InFileName");
			pid = getParameterValue("PID");
			processName = getParameterValue("ProcessName");
		} catch (ParameterNotFoundException e) {
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		String processContainerID = instantiateProcess(pid, processName);

		String fileContainerID = ifModel.getContainerIdByName(new PipName(-1,
				fileName));

		// check if container for filename exists and create new container
		if (fileContainerID == null) {
			fileContainerID = ifModel.addContainer(_messageFactory.createContainer());
			String fileDataID = ifModel.addData(_messageFactory.createData());

			ifModel.addDataToContainerMapping(fileDataID, fileContainerID);

			ifModel.addName(new PipName(-1, fileName), fileContainerID);
		}

		// add data to transitive reflexive closure of process container
		Set<String> reflexiveClosureOfProcessContainer = ifModel.getAliasClosure(processContainerID);
		Set<String> dataSet = ifModel.getDataInContainer(fileContainerID);
		for (String tempContainerID : reflexiveClosureOfProcessContainer) {
			ifModel.addDataToContainerMappings(dataSet, tempContainerID);
		}
		return _messageFactory.createStatus(EStatus.OKAY);
	}

	/**
	 * Checks if the process with given parameters already exists, if not create
	 * container, data and names for it.
	 * 
	 * @param PID
	 * @param processName
	 * @param ifModel
	 * @return
	 */
	private String instantiateProcess(String processId, String processName) {
		InformationFlowModel ifModel = getInformationFlowModel();
		String containerID = ifModel
				.getContainerIdByName(new PipName(-1, processId));

		// check if container for process exists and create new container if not
		if (containerID == null) {
			IContainer container = _messageFactory.createContainer();
			containerID = ifModel.addContainer(container);
			
			IData data = _messageFactory.createData();
			String dataId = ifModel.addData(data);
			
			
			ifModel.addDataToContainerMapping(dataId, containerID);
			ifModel.addName(new PipName(-1, processId), containerID);
			ifModel.addName(new PipName(Integer.parseInt(processId), processName),
					containerID);
		}

		return containerID;
	}

}
