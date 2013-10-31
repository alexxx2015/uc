package de.tum.in.i22.pip.core.eventdef;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class TestBeActionHandler extends BaseEventHandler {
	private static final Logger _logger = Logger
			.getLogger(EmptyCliboardAction.class);
	
	@Override
	public IStatus execute() {
		_logger.info("TestBeActionHandler execute");
		return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING);
	}

}
