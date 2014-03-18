package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;

/**
 *
 * @author Florian Kelbert
 *
 */
public class SendfileEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		int infd;
		int outfd;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			outfd = Integer.valueOf(getParameterValue("outfd"));
			infd = Integer.valueOf(getParameterValue("infd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		return LinuxEvents.copyDataTransitive(
				ifModel.getContainer(FiledescrName.create(host, pid, infd)),
				ifModel.getContainer(FiledescrName.create(host, pid, outfd)));
	}
}
