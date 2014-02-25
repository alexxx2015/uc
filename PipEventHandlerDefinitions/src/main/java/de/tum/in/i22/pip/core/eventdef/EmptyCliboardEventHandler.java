package de.tum.in.i22.pip.core.eventdef;


import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.cm.basic.ContainerName;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class EmptyCliboardEventHandler extends BaseEventHandler {

	private static final Logger _logger = Logger
			.getLogger(EmptyCliboardEventHandler.class);

	public EmptyCliboardEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("EmptyClipboard event handler execute");
		InformationFlowModel ifModel = getInformationFlowModel();
		String clipboardContainerId = ifModel.getContainerIdByName(new ContainerName(
				"clipboard"));

		// check if container for clipboard exists and create new container if not
		if (clipboardContainerId == null) {
			IContainer container = _messageFactory.createContainer();
			clipboardContainerId = ifModel.addContainer(container);
			ifModel.addName(new ContainerName("clipboard"), clipboardContainerId);
		}
		;

		ifModel.emptyContainer(clipboardContainerId);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
