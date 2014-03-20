package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.RemoteSocketContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketContainer.Domain;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketContainer.Type;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketName;


public class ConnectEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
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
			localConnectingSocket = (SocketContainer) ifModel.getContainer(FiledescrName.create(host, pid, fd));
		}
		catch (ClassCastException e) {
		}

		if (localConnectingSocket == null) {
			_logger.fatal("Container with identifier " + FiledescrName.create(host, pid, fd)
					+ " should exist due to previous socket() call. But it did not.");
			return STATUS_ERROR;
		}

		Domain domain = localConnectingSocket.getDomain();
		Type type = localConnectingSocket.getType();

		remoteSocketName = SocketName.create(remoteIP, remotePort, localIP, localPort);

		remoteAcceptedSocket = ifModel.getContainer(remoteSocketName);

		if (remoteAcceptedSocket != null && localIP.equals(remoteIP)) {
			// server is local AND
			// accept() happened before connect(); it created a temporary container.
			// Compensate for this. We can identify this temporary container by 'localSocketName'.

			IContainer tmpContainer = ifModel.getContainer(localSocketName);

			// copy aliases
			for (IContainer alias : ifModel.getAliasesFrom(tmpContainer)) {
				ifModel.addAlias(localConnectingSocket, alias);;
			}
			for (IContainer alias : ifModel.getAliasesTo(tmpContainer)) {
				ifModel.addAlias(alias, localConnectingSocket);
			}

			// copy data
			ifModel.addDataToContainerAndAliases(ifModel.getDataInContainer(tmpContainer), localConnectingSocket);

			// remove temporary container
			ifModel.remove(tmpContainer);
		}
		else {
			if (remoteAcceptedSocket == null) {
				if (localIP.equals(remoteIP)) {
					// server is local and connect() happens before accept().
					remoteAcceptedSocket = new SocketContainer(domain, type);
				}
				else {
					// server is remote.

					//TODO: restore settings
				//					remoteAcceptedSocket = new RemoteSocketContainer(remoteSocketName, domain, type,
//							new TcpConnector(remoteIP, PipSettings.getInstance().getPipRemotePortNum()));
				}

				// assign the remote name
				ifModel.addName(remoteSocketName, remoteAcceptedSocket);
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
			ifModel.addAlias(localConnectingSocket, remoteAcceptedSocket);
			ifModel.addAlias(remoteAcceptedSocket, localConnectingSocket);
		}

		// Assign the local socket container's name
		// CAUTION: We can *not* do this before compensating the temporary container!
		// Also, we do not do this if there already exists a proxy remote socket container
		// with the same name, as this means that two ip addreses are used locally and
		// the existing container was already created by accept()
		if (!(ifModel.getContainer(localSocketName) instanceof RemoteSocketContainer)) {
			// f[(p,(sn(e),(a,x))) <- c];
			ifModel.addName(localSocketName, localConnectingSocket);
		}


		return STATUS_OKAY;
	}
}