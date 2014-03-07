package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;

public class FcntlEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String operation = null;
		String oldfd = null;
		String newfd = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			operation = getParameterValue("operation");
			oldfd = getParameterValue("oldfd");
			newfd = getParameterValue("newfd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		switch (operation) {
		case "dupfd":
			IName oldName = FiledescrName.create(host, pid, oldfd);
			IName newName = FiledescrName.create(host, pid, newfd);
			ifModel.addName(oldName, newName);
			break;
		}

		

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}