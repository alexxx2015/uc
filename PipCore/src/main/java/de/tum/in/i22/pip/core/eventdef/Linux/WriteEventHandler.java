package de.tum.in.i22.pip.core.eventdef.Linux;

import java.util.Set;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;
import de.tum.in.i22.uc.cm.datatypes.Linux.RemoteSocketContainer;
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
		int fd;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer procCont = ifModel.getContainer(ProcessName.create(host, pid));
		if (procCont == null) {
			return STATUS_OKAY;
		}

		IContainer fileCont = ifModel.getContainer(FiledescrName.create(host, pid, fd));
		if (fileCont == null) {
			return STATUS_OKAY;
		}

		Set<IData> data = ifModel.getDataInContainer(procCont);
		if (data == null || data.size() == 0) {
			return STATUS_OKAY;
		}

		// copy into all containers aliased from the destination container
		for (IContainer c : ifModel.getAliasTransitiveClosure(fileCont)) {
			if (c instanceof RemoteSocketContainer) {
				System.out.println("Remote data transfer to " + c);
			}
			else {
				ifModel.addDataToContainerMappings(data, c);
			}
		}

		// now, also copy into the actual (direct) destination container ...
		// ... but only if it is not a socket.
		if (!(fileCont instanceof SocketContainer)) {
			ifModel.addDataToContainerMappings(data, fileCont);
		}

		return STATUS_OKAY;
	}
}
