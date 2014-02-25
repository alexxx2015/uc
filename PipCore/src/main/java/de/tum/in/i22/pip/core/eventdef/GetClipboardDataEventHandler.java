package de.tum.in.i22.pip.core.eventdef;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.ContainerName;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class GetClipboardDataEventHandler extends BaseEventHandler {


	public GetClipboardDataEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		String pid = null;
		String processName = null;

		try {
			 pid = getParameterValue("PID");
	         processName = getParameterValue("ProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
        String  processContainerId = instantiateProcess(pid, processName);

        InformationFlowModel ifModel = getInformationFlowModel();
        String clipboardContainerId = ifModel.getContainerIdByName(new ContainerName("clipboard"));

         //check if container for clipboard exists and create new container if not
         if (clipboardContainerId == null)
         {
        	 IContainer container = _messageFactory.createContainer();
             clipboardContainerId = ifModel.addContainer(container);
             ifModel.addName(new ContainerName("clipboard"), clipboardContainerId);
         };

         //add data to transitive reflexive closure of process container
         for (String tempContainerId : ifModel.getAliasTransitiveReflexiveClosure(processContainerId))
         {
             ifModel.addDataToContainerMappings(ifModel.getDataInContainer(clipboardContainerId), tempContainerId);
         }

         return _messageFactory.createStatus(EStatus.OKAY);
	}

}
