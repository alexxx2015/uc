package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class CreateProcessEventHandler extends WindowsEvents {

	public CreateProcessEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
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

        //add data of parent process container to child process container
        _informationFlowModel.addData(_informationFlowModel.getData(parentProcessContainer), processContainer);

        //add initial windows of process to model
        //TODO: REGEX??
        String[] visibleWindowsArray = visibleWindows.split(",",0);

        for (String handle : visibleWindowsArray)
        {
            IContainer windowContainer = _informationFlowModel.getContainer(new NameBasic(handle));

            if(windowContainer == null)
            {
            	windowContainer = _messageFactory.createContainer();
                _informationFlowModel.addName(new NameBasic(handle), windowContainer);
            }

            _informationFlowModel.addData(_informationFlowModel.getData(processContainer), windowContainer);

            _informationFlowModel.addAlias(processContainer, windowContainer);
        }

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}
