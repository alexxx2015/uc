package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ExitEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		LinuxEvents.exit(host, pid);

		return STATUS_OKAY;
	}

}