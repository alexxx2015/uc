package de.tum.in.i22.pip.core.eventdef;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class CreateProcessEventHandler extends BaseEventHandler {

	private static final Logger _logger = Logger
			.getLogger(CreateProcessEventHandler.class);

	public CreateProcessEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		String pid = null;
		String parentPid = null;
		String visibleWindows = null;
		// currently not used
		String processName = null;
		String parentProcessName = null;

		try {
			pid = getParameterValue("PID_Child");
	        parentPid = getParameterValue("PID");
	        visibleWindows = getParameterValue("VisibleWindows");

	        processName = getParameterValue("ChildProcessName");
	        parentProcessName = getParameterValue("ParentProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

        String processContainerId = instantiateProcess(pid, processName);
        String parentProcessContainerId = instantiateProcess(parentPid, parentProcessName);

        InformationFlowModel ifModel = getInformationFlowModel();
        //add data of parent process container to child process container
        ifModel.addDataToContainerMappings(ifModel.getDataInContainer(parentProcessContainerId), processContainerId);

        //add initial windows of process to model
        //TODO: REGEX??
        String[] visibleWindowsArray = visibleWindows.split(",",0);

        for (String handle : visibleWindowsArray)
        {
            String windowContainerId = ifModel.getContainerIdByName(new Name(handle));

            if(windowContainerId == null)
            {
            	IContainer container = _messageFactory.createContainer();
                windowContainerId = ifModel.addContainer(container);
                ifModel.addName(new Name(handle), windowContainerId);
            }

            ifModel.addDataToContainerMappings(ifModel.getDataInContainer(processContainerId), windowContainerId);

            ifModel.addAlias(processContainerId, windowContainerId);
        }

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}
