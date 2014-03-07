package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipeEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String fd1 = null;
		String fd2 = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd1 = getParameterValue("fd1");
			fd2 = getParameterValue("fd2");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer pipeContainer = _messageFactory.createContainer();

		if (pipeContainer != null) {
			ifModel.addName(FiledescrName.create(host, pid, fd1), pipeContainer);
			ifModel.addName(FiledescrName.create(host, pid, fd2), pipeContainer);
		}
		else {
			_logger.fatal("Unable to create pipe container.");
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}