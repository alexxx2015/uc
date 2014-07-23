package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.OSInternalName;
import de.tum.in.i22.uc.cm.datatypes.linux.PipeContainer;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class PipeEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int fd1;
		int fd2;
		String pipename = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd1 = Integer.valueOf(getParameterValue("fd1"));
			fd2 = Integer.valueOf(getParameterValue("fd2"));
			pipename = getParameterValue("pipename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer pipeContainer = new PipeContainer();

		_informationFlowModel.addName(FiledescrName.create(host, pid, fd1), pipeContainer);
		_informationFlowModel.addName(FiledescrName.create(host, pid, fd2), pipeContainer);
		_informationFlowModel.addName(OSInternalName.create(host, pipename), pipeContainer);

		return STATUS_OKAY;
	}

}