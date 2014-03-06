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
		String fdsrc = null;
		String fddst = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fdsrc = getParameterValue("fdsrc");
			fddst = getParameterValue("fddst");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer pipeContainer = _messageFactory.createContainer();

		if (pipeContainer != null) {
			ifModel.addName(FiledescrName.create(host, pid, fdsrc), pipeContainer);
			ifModel.addName(FiledescrName.create(host, pid, fddst), pipeContainer);
		}
		else {
			_logger.fatal("Unable to create pipe container.");
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}