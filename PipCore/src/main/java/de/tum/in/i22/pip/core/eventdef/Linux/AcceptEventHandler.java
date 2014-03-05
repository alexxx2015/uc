package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.distribution.ContainerRemote;
import de.tum.in.i22.uc.distribution.IPLocation;
import de.tum.in.i22.uc.distribution.Network;

public class AcceptEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String family = null;
		String localIP = null;
		String localPort = null;
		String remoteIP = null;
		String remotePort = null;
		String newFd = null;
		NameBasic localSocketName = null;
		NameBasic remoteSocketName = null;
		IContainer localContainer = null;
		IContainer remoteContainer = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			family = getParameterValue("family");
			localIP = getParameterValue("localIP");
			localPort = getParameterValue("localPort");
			remoteIP = getParameterValue("remoteIP");
			remotePort = getParameterValue("remotePort");
			newFd = getParameterValue("newfd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		if (!Network.SUPPORTED_SOCKET_FAMILIES.contains(family)) {
			_logger.info("Socket family " + family + " is not handled.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		// no IP address assigned. Syscall fails
		if (localIP.equals(Network.IP_UNSPEC)) {
			_logger.info("No local IP address was assigned. Syscall fails.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		InformationFlowModel ifModel = getInformationFlowModel();

		// local_socket_name := (sn(e),(a,x))
		localSocketName = SocketName.create(host, pid, localIP, localPort, remoteIP, remotePort);

		// remote_socket_name := ((a,x),sn(e))
		remoteSocketName = SocketName.create(host, pid, remoteIP, remotePort, localIP, localPort);

		// create new container c
		localContainer = _messageFactory.createContainer();

		if (localContainer == null) {
			_logger.fatal("Unable to create container.");
		}
		else {
			// f[(p,(sn(e),(a,x))) <- c]
			ifModel.addName(localSocketName, localContainer);

			// f[((p,e)) <- c]
			ifModel.addName(FiledescrName.create(host, pid, newFd), localContainer);

			if (!localIP.equals(remoteIP)) {
				// client is remote

				// get remote container id from remote host for creating the alias
				remoteContainer = new ContainerRemote(SocketName.create(host, newFd, remoteIP, remotePort, localIP, localPort), IPLocation.createIPLocation(remoteIP));

				ifModel.addAlias(localContainer, remoteContainer);
			}
			else {
				// client is local

				// this assumes that the corresponding connect() already happened.
				// This needs to be enforced by the PEP.
				remoteContainer = ifModel.getContainer(remoteSocketName);

				if (remoteContainer == null) {
					_logger.fatal("accept() happened, but corresponding connect() did not happen before. "
							+ "The order of these events must be enforced by the PEP.");
				}

				ifModel.addAlias(localContainer, remoteContainer);
				ifModel.addAlias(remoteContainer, localContainer);
			}

			// add name of remote container
			ifModel.addName(remoteSocketName, remoteContainer);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}

