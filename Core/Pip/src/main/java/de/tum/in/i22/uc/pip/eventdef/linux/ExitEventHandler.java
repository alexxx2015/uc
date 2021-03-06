package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class ExitEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		exit(host, pid);

		return STATUS_OKAY;
	}

}