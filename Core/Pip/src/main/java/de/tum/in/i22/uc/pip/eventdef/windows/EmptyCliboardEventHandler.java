package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;

public class EmptyCliboardEventHandler extends BaseEventHandler {

	public EmptyCliboardEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		IContainer clipboardContainer = basicIfModel.getContainer(new NameBasic(
				"clipboard"));

		// check if container for clipboard exists and create new container if not
		if (clipboardContainer == null) {
			clipboardContainer = _messageFactory.createContainer();
			basicIfModel.addName(new NameBasic("clipboard"), clipboardContainer);
		}
		;

		basicIfModel.emptyContainer(clipboardContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
