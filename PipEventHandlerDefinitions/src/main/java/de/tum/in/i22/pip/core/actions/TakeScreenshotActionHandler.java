package de.tum.in.i22.pip.core.actions;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class TakeScreenshotActionHandler extends BaseActionHandler {
	
	
	private static final Logger _logger = Logger
			.getLogger(TakeScreenshotActionHandler.class);
	
	public TakeScreenshotActionHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("TakeScreenshot action handler execute");
        String visibleWindow = null;
        try {
        	visibleWindow = getParameterValue("VisibleWindow");
        } catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
        }
        
        InformationFlowModel ifModel = getInformationFlowModel();
        String clipboardContainerId = ifModel.getContainerIdByName(new Name("clipboard"));

        //check if container for clipboard exists and create new container if not
        if (clipboardContainerId == null)
        {
        	IContainer container = _messageFactory.createContainer();
            clipboardContainerId = ifModel.addContainer(container);
            ifModel.addName(new Name("clipboard"), clipboardContainerId);
        };

        //do not empty as take screenshot events are split to one screenshot event per visible window
        //ifModel.emptyContainer(clipboardContainerID);

        String windowContainerId = ifModel.getContainerIdByName(new Name(visibleWindow));
        ifModel.addDataToContainerMappings(ifModel.getDataInContainer(windowContainerId), clipboardContainerId);

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}
