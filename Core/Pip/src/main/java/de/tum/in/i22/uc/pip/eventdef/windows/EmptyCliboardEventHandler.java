package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;

public class EmptyCliboardEventHandler extends BaseEventHandler {

	public EmptyCliboardEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		IContainer clipboardContainer = _informationFlowModel.getContainer(new NameBasic(
				"clipboard"));

		// check if container for clipboard exists and create new container if not
		if (clipboardContainer == null) {
			clipboardContainer = _messageFactory.createContainer();
			_informationFlowModel.addName(new NameBasic("clipboard"), clipboardContainer);
		}
		;

		_informationFlowModel.emptyContainer(clipboardContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
