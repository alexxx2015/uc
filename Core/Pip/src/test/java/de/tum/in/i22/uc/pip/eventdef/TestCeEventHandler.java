package de.tum.in.i22.uc.pip.eventdef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.windows.EmptyCliboardEventHandler;

public class TestCeEventHandler extends BaseEventHandler{
	private static final Logger _logger = LoggerFactory
			.getLogger(EmptyCliboardEventHandler.class);

	@Override
	public IStatus update() {
		_logger.info("TestCeEventHandler execute");
		return _messageFactory.createStatus(EStatus.ALLOW);
	}

}
