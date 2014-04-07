package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Domain;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Type;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SocketpairEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int fd1;
		int fd2;
		String domain = null;
		String type = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd1 = Integer.valueOf(getParameterValue("fd1"));
			fd2 = Integer.valueOf(getParameterValue("fd2"));
			domain = getParameterValue("domain");
			type = getParameterValue("type");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer sock1Container = new SocketContainer(Domain.from(domain), Type.from(type));
		IContainer sock2Container = new SocketContainer(Domain.from(domain), Type.from(type));

		if (sock1Container != null && sock2Container != null) {
			basicIfModel.addName(FiledescrName.create(host, pid, fd1), sock1Container);
			basicIfModel.addName(FiledescrName.create(host, pid, fd2), sock2Container);
			basicIfModel.addAlias(sock1Container, sock2Container);
			basicIfModel.addAlias(sock2Container, sock1Container);
		}
		else {
			_logger.error("Unable to create socket containers.");
		}

		return STATUS_OKAY;
	}

}
