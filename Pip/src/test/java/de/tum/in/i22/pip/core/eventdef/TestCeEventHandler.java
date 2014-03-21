package de.tum.in.i22.pip.core.eventdef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class TestCeEventHandler extends BaseEventHandler {
	private static final Logger _logger = LoggerFactory
			.getLogger(EmptyCliboardEventHandler.class);

	@Override
	public IStatus execute() {
		_logger.info("TestCeEventHandler execute");
		return _messageFactory.createStatus(EStatus.ALLOW);
	}

}
