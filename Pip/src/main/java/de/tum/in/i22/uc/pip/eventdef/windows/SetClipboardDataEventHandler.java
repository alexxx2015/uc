package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SetClipboardDataEventHandler extends BaseEventHandler {

	public SetClipboardDataEventHandler() {
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
        IContainer processContainer = WindowsEvents.instantiateProcess(pid, processName);

        IContainer clipboardContainer = basicIfModel.getContainer(new NameBasic("clipboard"));

        //check if container for clipboard exists and create new container if not
        if (clipboardContainer == null)
        {
        	clipboardContainer = _messageFactory.createContainer();
            basicIfModel.addName(new NameBasic("clipboard"), clipboardContainer);
        };

        basicIfModel.emptyContainer(clipboardContainer);
        basicIfModel.addDataToContainer(basicIfModel.getDataInContainer(processContainer), clipboardContainer);

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}
