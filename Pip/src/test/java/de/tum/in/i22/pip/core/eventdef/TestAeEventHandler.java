package de.tum.in.i22.pip.core.eventdef;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.EmptyCliboardEventHandler;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class TestAeEventHandler extends BaseEventHandler {
	private static final Logger _logger = Logger
			.getLogger(EmptyCliboardEventHandler.class);
	
	@Override
	public IStatus execute() {
		_logger.info("TestAeEventHandler execute");
		return _messageFactory.createStatus(EStatus.ERROR);
	}

}
