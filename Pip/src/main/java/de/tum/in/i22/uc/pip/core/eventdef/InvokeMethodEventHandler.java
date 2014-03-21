package de.tum.in.i22.uc.pip.core.eventdef;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class InvokeMethodEventHandler extends BaseEventHandler {

	public InvokeMethodEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
