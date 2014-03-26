package de.tum.in.i22.uc.pip.eventdef.linux;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ShutdownEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		int fd;
		String howStr = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			howStr = getParameterValue("how");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		Shut how = null;

		switch (howStr) {
		case "RD":
			how = Shut.SHUT_RD;
			break;
		case "WR":
			how = Shut.SHUT_WR;
		case "RDWR":
			how = Shut.SHUT_RDWR;
		}

		SocketContainer cont = (SocketContainer) basicIfModel.getContainer(FiledescrName.create(host, pid, fd));
		LinuxEvents.shutdownSocket(cont, how);

		return STATUS_OKAY;
	}

	public static enum Shut {
		SHUT_RDWR,
		SHUT_RD,
		SHUT_WR
	}
}

