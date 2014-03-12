package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 *
 * @author Florian Kelbert
 *
 */
public class OpenAtEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		String newfd = null;
		String dirfd = null;
		String filename = null;
		boolean at_fdcwd = false;
		boolean truncate = false;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			newfd = getParameterValue("newfd");
			dirfd = getParameterValue("dirfd");
			filename = getParameterValue("filename");
			truncate = Boolean.valueOf(getParameterValue("trunc"));
			at_fdcwd = Boolean.valueOf(getParameterValue("at_fdcwd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		LinuxEvents.open(host, pid, newfd, dirfd, filename, at_fdcwd, truncate);

		return STATUS_OKAY;
	}

}

