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
public class OpenEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		String fd = null;
		String filename = null;
		boolean truncate = false;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = getParameterValue("fd");
			filename = getParameterValue("filename");
			truncate = Boolean.valueOf(getParameterValue("trunc"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		LinuxEvents.open(host, pid, fd, null, filename, true, truncate);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}

