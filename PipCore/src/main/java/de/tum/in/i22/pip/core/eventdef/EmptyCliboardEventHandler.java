package de.tum.in.i22.pip.core.eventdef;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class EmptyCliboardEventHandler extends BaseEventHandler {

	public EmptyCliboardEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		InformationFlowModel ifModel = getInformationFlowModel();
		String clipboardContainerId = ifModel.getContainerIdByName(new Name(
				"clipboard"));

		// check if container for clipboard exists and create new container if not
		if (clipboardContainerId == null) {
			IContainer container = _messageFactory.createContainer();
			clipboardContainerId = ifModel.addContainer(container);
			ifModel.addName(new Name("clipboard"), clipboardContainerId);
		}
		;

		ifModel.emptyContainer(clipboardContainerId);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
