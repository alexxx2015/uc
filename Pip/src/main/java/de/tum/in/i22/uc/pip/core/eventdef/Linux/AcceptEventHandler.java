package de.tum.in.i22.uc.pip.core.eventdef.Linux;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketName;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Domain;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Type;
import de.tum.in.i22.uc.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.ParameterNotFoundException;

public class AcceptEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		String localIP = null;
		int localPort;
		String remoteIP = null;
		int remotePort;
		int newFd;
		int oldFd;
		IName localSocketName = null;
		SocketName remoteSocketName = null;
		IContainer localAcceptedSocket = null;
		IContainer remoteConnectedSocket = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			localIP = getParameterValue("localIP");
			localPort = Integer.valueOf(getParameterValue("localPort"));
			remoteIP = getParameterValue("remoteIP");
			remotePort = Integer.valueOf(getParameterValue("remotePort"));
			newFd = Integer.valueOf(getParameterValue("newfd"));
			oldFd = Integer.valueOf(getParameterValue("oldfd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// get old container
		SocketContainer listeningSocket = null;
		try {
			listeningSocket = (SocketContainer) ifModel.getContainer(FiledescrName.create(host, pid, oldFd));
		}
		catch (ClassCastException e) {
			_logger.error("Expected container did not exist or was of wrong type.");
			return STATUS_ERROR;
		}

		if (listeningSocket == null) {
			_logger.error("Expected container did not exist or was of wrong type.");
			return STATUS_ERROR;
		}

		Domain domain = listeningSocket.getDomain();
		Type type = listeningSocket.getType();

		// local_socket_name := (sn(e),(a,x))
		localSocketName = SocketName.create(localIP, localPort, remoteIP, remotePort);

		// remote_socket_name := ((a,x),sn(e))
		remoteSocketName = SocketName.create(remoteIP, remotePort, localIP, localPort);


		if (!localIP.equals(remoteIP)) {
			// client is remote

			// create a 'proxy' container and name it.
			//TODO: restore settings
			//			remoteConnectedSocket = new RemoteSocketContainer(remoteSocketName, domain, type,
//					new TcpConnector(remoteIP, PipSettings.getInstance().getPipRemotePortNum()));
			ifModel.addName(remoteSocketName, remoteConnectedSocket);

			// create new local container c and name it, f[(p,(sn(e),(a,x))) <- c]
			localAcceptedSocket = new SocketContainer(domain, type);
			ifModel.addName(localSocketName, localAcceptedSocket);

			// add alias from new local container to remote proxy container
			ifModel.addAlias(localAcceptedSocket, remoteConnectedSocket);
		}
		else {
			// client is local

			// see whether local container was already created
			// which is the case if connect() already happened
			localAcceptedSocket = ifModel.getContainer(localSocketName);

			if (localAcceptedSocket == null) {
				// accept() happens before connect().

				// create new container c and name it, f[(p,(sn(e),(a,x))) <- c]
				localAcceptedSocket = new SocketContainer(domain, type);
				ifModel.addName(localSocketName, localAcceptedSocket);

				/*
				 * We create a temporary remote socket container.
				 *
				 * The problem here is that we do not know the remote process id and the corresponding file descriptor.
				 * Yet, it may be the case that our side writes to the socket while we did not yet observe
				 * the remote connect().
				 *
				 * This temporary container will be deleted upon connect().
				 * The container can be identified upon connect() using the socket name that
				 * is called 'remoteSocketName' and that will be called 'localSocketName' upon connect().
				 */
				remoteConnectedSocket = new SocketContainer(domain, type);
				ifModel.addName(remoteSocketName, remoteConnectedSocket);

				// add aliases in both directions
				ifModel.addAlias(localAcceptedSocket, remoteConnectedSocket);
				ifModel.addAlias(remoteConnectedSocket, localAcceptedSocket);
			}
			// else: connect() happened before accept(); it created the container and both aliases
		}

		// f[((p,e)) <- c]
		ifModel.addName(FiledescrName.create(host, pid, newFd), localAcceptedSocket);

		return STATUS_OKAY;
	}
}

