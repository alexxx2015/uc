package de.tum.in.i22.pip.core.eventdef;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class InvokeMethodEventHandler extends BaseEventHandler {
	private static final Logger _logger = Logger
			.getLogger(InvokeMethodEventHandler.class);

	public InvokeMethodEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
