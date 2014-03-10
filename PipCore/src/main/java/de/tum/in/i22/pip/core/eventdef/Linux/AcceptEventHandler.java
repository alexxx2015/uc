package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.RemoteContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketName;
import de.tum.in.i22.uc.distribution.IPLocation;

public class AcceptEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String localIP = null;
		String localPort = null;
		String remoteIP = null;
		String remotePort = null;
		String newFd = null;
		String oldFd = null;
		NameBasic localSocketName = null;
		NameBasic remoteSocketName = null;
		IContainer localContainer = null;
		IContainer remoteContainer = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			localIP = getParameterValue("localIP");
			localPort = getParameterValue("localPort");
			remoteIP = getParameterValue("remoteIP");
			remotePort = getParameterValue("remotePort");
			newFd = getParameterValue("newfd");
			oldFd = getParameterValue("oldfd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// get old container
		SocketContainer oldContainer = null;
		try {
			oldContainer = (SocketContainer) ifModel.getContainer(FiledescrName.create(host, pid, oldFd));
		}
		catch (ClassCastException e) {
			_logger.fatal("Expected container did not exist or was of wrong type.");
			return _messageFactory.createStatus(EStatus.ERROR);
		}

		if (oldContainer == null) {
			_logger.fatal("Expected container did not exist or was of wrong type.");
			return _messageFactory.createStatus(EStatus.ERROR);
		}

		// local_socket_name := (sn(e),(a,x))
		localSocketName = SocketName.create(host, localIP, localPort, remoteIP, remotePort);

		// remote_socket_name := ((a,x),sn(e))
		remoteSocketName = SocketName.create(host, remoteIP, remotePort, localIP, localPort);

		// create new container c
		localContainer = new SocketContainer(oldContainer.getDomain(), oldContainer.getType());

		// f[(p,(sn(e),(a,x))) <- c]
		ifModel.addName(localSocketName, localContainer);

		// f[((p,e)) <- c]
		ifModel.addName(FiledescrName.create(host, pid, newFd), localContainer);

		if (!localIP.equals(remoteIP)) {
			// client is remote

			// create remote container for creating the alias
			remoteContainer = new RemoteContainer(SocketName.create(host, remoteIP, remotePort, localIP, localPort), IPLocation.createIPLocation(remoteIP));

			ifModel.addAlias(localContainer, remoteContainer);

			// TODO remote call
		}
		else {
			// client is local

			// this assumes that the corresponding connect() already happened.
			// This needs to be enforced by the PEP.
			remoteContainer = ifModel.getContainer(remoteSocketName);

			if (remoteContainer == null) {
				_logger.fatal("accept() happened, but corresponding connect() did not happen before. "
						+ "The order of these events must be enforced by the PEP.");
				return _messageFactory.createStatus(EStatus.ERROR);
			}

			ifModel.addAlias(localContainer, remoteContainer);
			ifModel.addAlias(remoteContainer, localContainer);
		}

		// add name of remote container
		ifModel.addName(remoteSocketName, remoteContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}

