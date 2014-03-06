package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ReadEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String fd = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd = getParameterValue("fd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IName file = FiledescrName.create(host, pid, fd);
		IName proc = ProcessName.create(host, pid);

		if (ifModel.copyData(file, proc)) {
			_logger.info("Copied data from " + file + " to " + proc);
		}
		else {
			_logger.info("Did not copy data from " + file + " to " + proc);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
