package de.tum.in.i22.pip.core.eventdef;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class TakeScreenshotEventHandler extends BaseEventHandler {
	
	
	private static final Logger _logger = Logger
			.getLogger(TakeScreenshotEventHandler.class);
	
	public TakeScreenshotEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("TakeScreenshot event handler execute");
        String visibleWindow = null;
        try {
        	visibleWindow = getParameterValue("VisibleWindow");
        } catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
        }
        
        InformationFlowModel ifModel = getInformationFlowModel();
        String clipboardContainerId = ifModel.getContainerIdByName(new NameBasic("clipboard"));

        //check if container for clipboard exists and create new container if not
        if (clipboardContainerId == null)
        {
        	IContainer container = _messageFactory.createContainer();
            clipboardContainerId = ifModel.addContainer(container);
            ifModel.addName(new NameBasic("clipboard"), clipboardContainerId);
        };

        //do not empty as take screenshot events are split to one screenshot event per visible window
        //ifModel.emptyContainer(clipboardContainerID);

        String windowContainerId = ifModel.getContainerIdByName(new NameBasic(visibleWindow));
        ifModel.addDataToContainerMappings(ifModel.getDataInContainer(windowContainerId), clipboardContainerId);

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}
