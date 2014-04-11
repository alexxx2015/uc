package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class GetClipboardDataEventHandler extends BaseEventHandler {


	public GetClipboardDataEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String pid = null;
		String processName = null;

		try {
			 pid = getParameterValue("PID");
	         processName = getParameterValue("ProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
        IContainer  processContainer = WindowsEvents.instantiateProcess(pid, processName);

        IContainer clipboardContainer = basicIfModel.getContainer(new NameBasic("clipboard"));

         //check if container for clipboard exists and create new container if not
         if (clipboardContainer == null)
         {
        	 clipboardContainer = _messageFactory.createContainer();
             basicIfModel.addName(new NameBasic("clipboard"), clipboardContainer);
         };

         //add data to transitive reflexive closure of process container
         for (IContainer tempContainer : basicIfModel.getAliasTransitiveReflexiveClosure(processContainer))
         {
             basicIfModel.addData(basicIfModel.getData(clipboardContainer), tempContainer);
         }

         return _messageFactory.createStatus(EStatus.OKAY);
	}

}
