package de.tum.in.i22.pip.core.eventdef;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class InvokeMethodActionHandler extends BaseEventHandler {
	private static final Logger _logger = Logger
			.getLogger(KillProcessActionHandler.class);
	
	public InvokeMethodActionHandler() {
		super();
	}
	
	@Override
	public IStatus execute() {
		_logger.info("InvokeMethod action handler execute");
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
