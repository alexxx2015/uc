package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketContainer;

/**
 *
 * @author Florian Kelbert
 *
 */
public class SendfileEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		String infd = null;
		String outfd = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			outfd = getParameterValue("outfd");
			infd = getParameterValue("infd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IName inFd = FiledescrName.create(host, pid, infd);
		IName outFd = FiledescrName.create(host, pid, outfd);

		IContainer srcCont = ifModel.getContainer(inFd);
		IContainer dstCont = ifModel.getContainer(outFd);

		// copy the data into all aliased containers aliased from the destination container
		for (IContainer dst : ifModel.getAliasTransitiveClosure(dstCont)) {
			ifModel.copyData(srcCont, dst);
		}

		// now, also copy into the actual (direct) destination container ...
		// ... but only if it is not a socket.
		if (!(dstCont instanceof SocketContainer)) {
			ifModel.copyData(srcCont, dstCont);
		}

		return STATUS_OKAY;
	}
}
