package de.tum.in.i22.pip.core.actions;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class GetClipboardDataActionHandler extends BaseActionHandler {
	
	private static final Logger _logger = Logger
			.getLogger(GetClipboardDataActionHandler.class);

	
	public GetClipboardDataActionHandler() {
		super();
	}
	
	@Override
	public IStatus execute() {
		_logger.info("GetClipboardData action handler execute");
		
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
        String clipboardContainerId = ifModel.getContainerIdByName(new Name("clipboard"));

         //check if container for clipboard exists and create new container if not
         if (clipboardContainerId == null)
         {
        	 IContainer container = _messageFactory.createContainer();
             clipboardContainerId = ifModel.addContainer(container);
             ifModel.addName(new Name("clipboard"), clipboardContainerId);
         };

         //add data to transitive reflexive closure of process container
         for (String tempContainerId : ifModel.getAliasTransitiveReflexiveClosure(processContainerId))
         {
             ifModel.addDataToContainerMappings(ifModel.getDataInContainer(clipboardContainerId), tempContainerId);
         }
         
         return _messageFactory.createStatus(EStatus.OKAY);
	}

}
