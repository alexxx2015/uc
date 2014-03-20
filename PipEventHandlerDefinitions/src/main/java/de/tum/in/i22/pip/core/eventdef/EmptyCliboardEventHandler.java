package de.tum.in.i22.pip.core.eventdef;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class EmptyCliboardEventHandler extends BaseEventHandler {

	public EmptyCliboardEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("EmptyClipboard event handler execute");
		IContainer clipboardContainer = ifModel.getContainer(new NameBasic(
				"clipboard"));

		// check if container for clipboard exists and create new container if not
		if (clipboardContainer == null) {
			clipboardContainer = _messageFactory.createContainer();
			ifModel.addName(new NameBasic("clipboard"), clipboardContainer);
		}
		;

		ifModel.emptyContainer(clipboardContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
