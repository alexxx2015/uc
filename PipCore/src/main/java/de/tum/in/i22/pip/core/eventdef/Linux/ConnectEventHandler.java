package de.tum.in.i22.pip.core.eventdef.Linux;


import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ConnectEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String fd = null;
		String family = null;
		String localIP = null;
		String localPort = null;
		String remoteIP = null;
		String remotePort = null;
		Name socketFD = null;
		Name localSocketName = null;
		String localContainerId = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd = getParameterValue("fd");
			family = getParameterValue("family");
			localIP = getParameterValue("localIP");
			localPort = getParameterValue("localPort");
			remoteIP = getParameterValue("remoteIP");
			remotePort = getParameterValue("remotePort");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		if (!LinuxEvents.SUPPORTED_SOCKET_FAMILIES.contains(family)) {
			_logger.info("Socket family " + family + " is not handled.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		// no IP address assigned. Syscall fails
		if (localIP.equals(LinuxEvents.IP_UNSPEC)) {
			_logger.info("No local IP address was assigned. Syscall fails.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		InformationFlowModel ifModel = getInformationFlowModel();

		// localSocketName := (sn(e),(a,x))
		localSocketName = LinuxEvents.createSocketIdentifier(host, pid, localIP, localPort, remoteIP, remotePort);

		// c := f((pid,sfd))
		socketFD = LinuxEvents.createFiledescrIdentifier(host, pid, fd);
		localContainerId = ifModel.getContainerIdByName(socketFD);

		if (localContainerId == null) {
			_logger.fatal("Container with identifier " + socketFD + " should exist due to "
					+ "previous socket() call. But it did not.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		// f[(p,(sn(e),(a,x))) <- c]
		ifModel.addName(localSocketName, localContainerId);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}