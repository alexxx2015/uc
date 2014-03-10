package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketName;


public class ConnectEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String fd = null;
		String localIP = null;
		String localPort = null;
		String remoteIP = null;
		String remotePort = null;
		NameBasic socketFD = null;
		NameBasic localSocketName = null;
		IContainer localContainer = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd = getParameterValue("fd");
			localIP = getParameterValue("localIP");
			localPort = getParameterValue("localPort");
			remoteIP = getParameterValue("remoteIP");
			remotePort = getParameterValue("remotePort");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// localSocketName := (sn(e),(a,x))
		localSocketName = SocketName.create(host, pid, localIP, localPort, remoteIP, remotePort);

		// c := f((pid,sfd))
		socketFD = FiledescrName.create(host, pid, fd);
		localContainer = ifModel.getContainer(socketFD);

		if (localContainer == null) {
			_logger.fatal("Container with identifier " + socketFD + " should exist due to "
					+ "previous socket() call. But it did not.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		// f[(p,(sn(e),(a,x))) <- c]
		ifModel.addName(localSocketName, localContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}