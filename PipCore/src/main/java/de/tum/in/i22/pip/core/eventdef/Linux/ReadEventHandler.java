package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ReadEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		String fd = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = getParameterValue("fd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer fileCont = ifModel.getContainer(FiledescrName.create(host, pid, fd));
		IContainer procCont = ifModel.getContainer(ProcessName.create(host, pid));

		if (fileCont != null) {
			for (IContainer c : ifModel.getAliasTransitiveReflexiveClosure(procCont)) {
				ifModel.copyData(fileCont, c);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
