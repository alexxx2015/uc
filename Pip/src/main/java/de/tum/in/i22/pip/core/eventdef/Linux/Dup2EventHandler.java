package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;

public class Dup2EventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		int oldfd;
		int newfd;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			oldfd = Integer.valueOf(getParameterValue("oldfd"));
			newfd = Integer.valueOf(getParameterValue("newfd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IName oldName = FiledescrName.create(host, pid, oldfd);
		IName newName = FiledescrName.create(host, pid, newfd);

		ifModel.removeName(newName);
		ifModel.addName(oldName, newName);

		return STATUS_OKAY;
	}

}