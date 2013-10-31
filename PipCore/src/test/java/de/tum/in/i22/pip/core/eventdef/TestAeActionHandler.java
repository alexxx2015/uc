package de.tum.in.i22.pip.core.eventdef;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.EmptyCliboardAction;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class TestAeActionHandler extends BaseEventHandler {
	private static final Logger _logger = Logger
			.getLogger(EmptyCliboardAction.class);
	
	@Override
	public IStatus execute() {
		_logger.info("TestAeActionHandler execute");
		return _messageFactory.createStatus(EStatus.ERROR);
	}

}
