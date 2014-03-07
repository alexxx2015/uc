package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.distribution.SocketContainer;
import de.tum.in.i22.uc.distribution.SocketContainer.Domain;
import de.tum.in.i22.uc.distribution.SocketContainer.Type;

public class SocketpairEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String fd1 = null;
		String fd2 = null;
		String domain = null;
		String type = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd1 = getParameterValue("fd1");
			fd2 = getParameterValue("fd2");
			domain = getParameterValue("domain");
			type = getParameterValue("type");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer sock1Container = new SocketContainer(Domain.from(domain), Type.from(type));
		IContainer sock2Container = new SocketContainer(Domain.from(domain), Type.from(type));

		if (sock1Container != null && sock2Container != null) {
			ifModel.addName(FiledescrName.create(host, pid, fd1), sock1Container);
			ifModel.addName(FiledescrName.create(host, pid, fd2), sock2Container);
			ifModel.addAlias(sock1Container, sock2Container);
			ifModel.addAlias(sock2Container, sock1Container);
		}
		else {
			_logger.fatal("Unable to create socket containers.");
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
