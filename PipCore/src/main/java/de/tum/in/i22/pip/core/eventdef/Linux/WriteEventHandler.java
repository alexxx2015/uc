package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketContainer;

/**
 *
 * @author Florian Kelbert
 *
 */
public class WriteEventHandler extends BaseEventHandler {

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

		IContainer procCont = ifModel.getContainer(ProcessName.create(host, pid));
		IContainer fileCont = ifModel.getContainer(FiledescrName.create(host, pid, fd));

		if (procCont != null) {
			// copy the data into all containers aliased from the destination container
			for (IContainer c : ifModel.getAliasTransitiveClosure(fileCont)) {
				ifModel.copyData(procCont, c);
			}

			// now, also copy into the actual (direct) destination container ...
			// ... but only if it is not a socket.
			if (!(fileCont instanceof SocketContainer)) {
				ifModel.copyData(procCont, fileCont);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
