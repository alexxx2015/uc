package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.RemoteSocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Domain;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Type;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketName;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;


public class ConnectEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int fd;
		String localIP = null;
		int localPort;
		String remoteIP = null;
		int remotePort;
		IName localSocketName = null;
		SocketName remoteSocketName = null;
		SocketContainer localConnectingSocket = null;
		IContainer remoteAcceptedSocket = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			localIP = getParameterValue("localIP");
			localPort = Integer.valueOf(getParameterValue("localPort"));
			remoteIP = getParameterValue("remoteIP");
			remotePort = Integer.valueOf(getParameterValue("remotePort"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// localSocketName := (sn(e),(a,x))
		localSocketName = SocketName.create(localIP, localPort, remoteIP, remotePort);

		// c := f((pid,sfd))
		try {
			localConnectingSocket = (SocketContainer) basicIfModel.getContainer(FiledescrName.create(host, pid, fd));
		}
		catch (ClassCastException e) {
		}

		if (localConnectingSocket == null) {
			_logger.error("Container with identifier " + FiledescrName.create(host, pid, fd)
					+ " should exist due to previous socket() call. But it did not.");
			return STATUS_ERROR;
		}

		Domain domain = localConnectingSocket.getDomain();
		Type type = localConnectingSocket.getType();

		remoteSocketName = SocketName.create(remoteIP, remotePort, localIP, localPort);

		remoteAcceptedSocket = basicIfModel.getContainer(remoteSocketName);

		if (remoteAcceptedSocket != null && localIP.equals(remoteIP)) {
			// server is local AND
			// accept() happened before connect(); it created a temporary container.
			// Compensate for this. We can identify this temporary container by 'localSocketName'.

			IContainer tmpContainer = basicIfModel.getContainer(localSocketName);

			// copy aliases
			for (IContainer alias : basicIfModel.getAliasesFrom(tmpContainer)) {
				basicIfModel.addAlias(localConnectingSocket, alias);;
			}
			for (IContainer alias : basicIfModel.getAliasesTo(tmpContainer)) {
				basicIfModel.addAlias(alias, localConnectingSocket);
			}

			// copy data
			basicIfModel.addDataTransitively(basicIfModel.getData(tmpContainer), localConnectingSocket);

			// remove temporary container
			basicIfModel.remove(tmpContainer);
		}
		else {
			if (remoteAcceptedSocket == null) {
				if (localIP.equals(remoteIP)) {
					// server is local and connect() happens before accept().
					remoteAcceptedSocket = new SocketContainer(domain, type);
				}
				else {
					// server is remote.

					remoteAcceptedSocket = new RemoteSocketContainer(remoteSocketName, domain, type,
							new IPLocation(remoteIP, Settings.getInstance().getPipListenerPort()));
				}

				// assign the remote name
				basicIfModel.addName(remoteSocketName, remoteAcceptedSocket);
			}
			/*
			 * Comment left for clarification.
			 * else {
			 *     // server is remote, but remote socket container already exists locally.
			 *     // Usually, this should not be the case.
			 *     // Yet, if a machine has multiple IP addresses, or if we do simulations,
			 *     // this might in fact be the case.
			 * }
			 */

			// add aliases in both directions
			basicIfModel.addAlias(localConnectingSocket, remoteAcceptedSocket);
			basicIfModel.addAlias(remoteAcceptedSocket, localConnectingSocket);
		}

		// Assign the local socket container's name
		// CAUTION: We can *not* do this before compensating the temporary container!
		// Also, we do not do this if there already exists a proxy remote socket container
		// with the same name, as this means that two ip addreses are used locally and
		// the existing container was already created by accept()
		if (!(basicIfModel.getContainer(localSocketName) instanceof RemoteSocketContainer)) {
			// f[(p,(sn(e),(a,x))) <- c];
			basicIfModel.addName(localSocketName, localConnectingSocket);
		}


		return STATUS_OKAY;
	}
}
