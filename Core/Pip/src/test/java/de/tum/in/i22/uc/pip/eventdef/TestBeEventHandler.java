package de.tum.in.i22.uc.pip.eventdef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.windows.EmptyCliboardEventHandler;

public class TestBeEventHandler extends BaseEventHandler{
	private static final Logger _logger = LoggerFactory
			.getLogger(EmptyCliboardEventHandler.class);

	@Override
	public IStatus execute() {
		_logger.info("TestBeEventHandler execute");
		return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING);
	}

}
