package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Domain;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer.Type;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketName;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;


public class ConnectEventHandler extends LinuxEvents {

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
		SocketContainer remoteAcceptedSocket = null;
		String socketname;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			localIP = getParameterValue("localIP");
			localPort = Integer.valueOf(getParameterValue("localPort"));
			remoteIP = getParameterValue("remoteIP");
			remotePort = Integer.valueOf(getParameterValue("remotePort"));
			socketname = getParameterValue("socketname");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// localSocketName := (sn(e),(a,x))
		localSocketName = SocketName.create(localIP, localPort, remoteIP, remotePort);

		// c := f((pid,sfd))
		try {
			localConnectingSocket = (SocketContainer) _informationFlowModel.getContainer(FiledescrName.create(host, pid, fd));
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

		remoteAcceptedSocket = (SocketContainer) _informationFlowModel.getContainer(remoteSocketName);

		IPLocation remoteResponsibleLocation = _distributionManager.getResponsibleLocation(remoteIP);


		if (remoteAcceptedSocket != null && sameResponsibleLocation(remoteResponsibleLocation, IPLocation.localIpLocation)) {
			// server is local AND
			// accept() happened before connect(); it created a temporary container.
			// Compensate for this. We can identify this temporary container by 'localSocketName'.

			IContainer tmpContainer = _informationFlowModel.getContainer(localSocketName);

			// copy aliases
			for (IContainer alias : _informationFlowModel.getAliasesFrom(tmpContainer)) {
				_informationFlowModel.addAlias(localConnectingSocket, alias);;
			}
			for (IContainer alias : _informationFlowModel.getAliasesTo(tmpContainer)) {
				_informationFlowModel.addAlias(alias, localConnectingSocket);
			}

			// copy data
			_informationFlowModel.addDataTransitively(_informationFlowModel.getData(tmpContainer), localConnectingSocket);

			// remove temporary container
			_informationFlowModel.remove(tmpContainer);

			remoteAcceptedSocket.setSocketName(remoteSocketName);
		}
		else {
			if (remoteAcceptedSocket == null) {
				if (sameResponsibleLocation(remoteResponsibleLocation, IPLocation.localIpLocation)) {
					// server is local and connect() happens before accept().
					remoteAcceptedSocket = new SocketContainer(domain, type, IPLocation.localIpLocation, remoteSocketName);
				}
				else {
					// server is remote.
					remoteAcceptedSocket = new SocketContainer(domain, type, remoteResponsibleLocation, remoteSocketName);
				}

				// assign the remote name
				_informationFlowModel.addName(remoteSocketName, remoteAcceptedSocket);
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
			_informationFlowModel.addAlias(localConnectingSocket, remoteAcceptedSocket);
			_informationFlowModel.addAlias(remoteAcceptedSocket, localConnectingSocket);
		}

		// Assign the local socket container's name
		// CAUTION: We can *not* do this before compensating the temporary container!
		// Also, we do not do this if there already exists a proxy remote socket container
		// with the same name, as this means that two ip addresses are used locally and
		// the existing container was already created by accept()
		if (!(_informationFlowModel.getContainer(localSocketName) instanceof SocketContainer)) {
			// f[(p,(sn(e),(a,x))) <- c];
			_informationFlowModel.addName(localSocketName, localConnectingSocket);
		}

		return STATUS_OKAY;
	}
}
