package de.tum.in.i22.pip.core.eventdef;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class TakeScreenshotEventHandler extends BaseEventHandler {

	public TakeScreenshotEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
        String visibleWindow = null;
        try {
        	visibleWindow = getParameterValue("VisibleWindow");
        } catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
        }

        InformationFlowModel ifModel = getInformationFlowModel();
        IContainer clipboardContainer = ifModel.getContainer(new NameBasic("clipboard"));

        //check if container for clipboard exists and create new container if not
        if (clipboardContainer == null)
        {
        	clipboardContainer = _messageFactory.createContainer();
            ifModel.add(clipboardContainer);
            ifModel.addName(new NameBasic("clipboard"), clipboardContainer);
        };

        //do not empty as take screenshot events are split to one screenshot event per visible window
        //ifModel.emptyContainer(clipboardContainerID);

        IContainer windowContainer = ifModel.getContainer(new NameBasic(visibleWindow));
        ifModel.addDataToContainerMappings(ifModel.getDataInContainer(windowContainer), clipboardContainer);

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}
