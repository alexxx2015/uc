package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class TakeScreenshotEventHandler extends BaseEventHandler {

	public TakeScreenshotEventHandler() {
		super();
	}

	@Override
	protected IStatus execute() {
        String visibleWindow = null;
        try {
        	visibleWindow = getParameterValue("VisibleWindow");
        } catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
        }

        IContainer clipboardContainer = basicIfModel.getContainer(new NameBasic("clipboard"));

        //check if container for clipboard exists and create new container if not
        if (clipboardContainer == null)
        {
        	clipboardContainer = _messageFactory.createContainer();
            basicIfModel.addName(new NameBasic("clipboard"), clipboardContainer);
        };

        //do not empty as take screenshot events are split to one screenshot event per visible window
        //ifModel.emptyContainer(clipboardContainerID);

        IContainer windowContainer = basicIfModel.getContainer(new NameBasic(visibleWindow));
        basicIfModel.addDataToContainer(basicIfModel.getDataInContainer(windowContainer), clipboardContainer);

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}
