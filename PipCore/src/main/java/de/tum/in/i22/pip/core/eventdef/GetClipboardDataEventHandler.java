package de.tum.in.i22.pip.core.eventdef;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
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
        IContainer  processContainer = instantiateProcess(pid, processName);

        IContainer clipboardContainer = ifModel.getContainer(new NameBasic("clipboard"));

         //check if container for clipboard exists and create new container if not
         if (clipboardContainer == null)
         {
        	 clipboardContainer = _messageFactory.createContainer();
             ifModel.addName(new NameBasic("clipboard"), clipboardContainer);
         };

         //add data to transitive reflexive closure of process container
         for (IContainer tempContainer : ifModel.getAliasTransitiveReflexiveClosure(processContainer))
         {
             ifModel.addDataToContainerMappings(ifModel.getDataInContainer(clipboardContainer), tempContainer);
         }

         return _messageFactory.createStatus(EStatus.OKAY);
	}

}
