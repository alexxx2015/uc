package de.tum.in.i22.pip.core.eventdef;


import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class CreateProcessEventHandler extends BaseEventHandler {

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

        IContainer processContainer = instantiateProcess(pid, processName);
        IContainer parentProcessContainer = instantiateProcess(parentPid, parentProcessName);

        InformationFlowModel ifModel = getInformationFlowModel();
        //add data of parent process container to child process container
        ifModel.addDataToContainerMappings(ifModel.getDataInContainer(parentProcessContainer), processContainer);

        //add initial windows of process to model
        //TODO: REGEX??
        String[] visibleWindowsArray = visibleWindows.split(",",0);

        for (String handle : visibleWindowsArray)
        {
            IContainer windowContainer = ifModel.getContainer(new NameBasic(handle));

            if(windowContainer == null)
            {
            	IContainer container = _messageFactory.createContainer();
                ifModel.addContainer(container);
                ifModel.addName(new NameBasic(handle), windowContainer);
            }

            ifModel.addDataToContainerMappings(ifModel.getDataInContainer(processContainer), windowContainer);

            ifModel.addAlias(processContainer, windowContainer);
        }

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}
