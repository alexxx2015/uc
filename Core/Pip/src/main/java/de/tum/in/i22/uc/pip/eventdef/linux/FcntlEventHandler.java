package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class FcntlEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		String operation = null;
		int oldfd;
		int newfd;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			operation = getParameterValue("operation");
			oldfd = Integer.valueOf(getParameterValue("oldfd"));
			newfd = Integer.valueOf(getParameterValue("newfd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		switch (operation) {
			case "dupfd":
				IName oldName = FiledescrName.create(host, pid, oldfd);
				IName newName = FiledescrName.create(host, pid, newfd);
				basicIfModel.addName(oldName, newName);
				break;
		}

		return STATUS_OKAY;
	}

}