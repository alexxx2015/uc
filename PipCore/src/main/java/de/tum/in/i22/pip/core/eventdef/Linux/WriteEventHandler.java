package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
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
		String pid = null;
		String fd = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd = getParameterValue("fd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		IName dstFd = FiledescrName.create(host, pid, fd);
		IName srcPid = ProcessName.create(host, pid);

		IContainer srcCont = ifModel.getContainer(srcPid);
		IContainer dstCont = ifModel.getContainer(dstFd);		
				
		// copy the data into all aliased containers aliased from the destination container
		for (IContainer dst : ifModel.getAliasTransitiveClosure(dstCont)) {
			ifModel.copyData(srcCont, dst);
		}
		
		// now, also copy into the actual (direct) destination container ...
		// ... but only if it is not a socket.		
		if (!(dstCont instanceof SocketContainer)) {
			ifModel.copyData(srcCont, dstCont);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
