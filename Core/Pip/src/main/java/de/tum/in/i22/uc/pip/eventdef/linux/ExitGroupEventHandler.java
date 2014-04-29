package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class ExitGroupEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
		String host = null;
		String pids = null;

		try {
			host = getParameterValue("host");
			pids = getParameterValue("pids");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		for (String pid : pids.split(":")) {
			exit(host, Integer.valueOf(pid));
		}

		return STATUS_OKAY;
	}

}