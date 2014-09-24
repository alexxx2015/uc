package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.OSInternalName;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Domain;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Type;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
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
		String socketname1 = null;
		String socketname2 = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd1 = Integer.valueOf(getParameterValue("fd1"));
			fd2 = Integer.valueOf(getParameterValue("fd2"));
			socketname1 = getParameterValue("socketname1");
			socketname2 = getParameterValue("socketname2");
			domain = getParameterValue("domain");
			type = getParameterValue("type");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		SocketContainer sock1Container = new SocketContainer(Domain.from(domain), Type.from(type), IPLocation.localIpLocation);
		SocketContainer sock2Container = new SocketContainer(Domain.from(domain), Type.from(type), IPLocation.localIpLocation);

		_informationFlowModel.addName(FiledescrName.create(host, pid, fd1), sock1Container);
		_informationFlowModel.addName(FiledescrName.create(host, pid, fd2), sock2Container);
		_informationFlowModel.addName(OSInternalName.create(host, socketname1), sock1Container);
		_informationFlowModel.addName(OSInternalName.create(host, socketname2), sock2Container);
		_informationFlowModel.addAlias(sock1Container, sock2Container);
		_informationFlowModel.addAlias(sock2Container, sock1Container);

		return STATUS_OKAY;
	}

}
