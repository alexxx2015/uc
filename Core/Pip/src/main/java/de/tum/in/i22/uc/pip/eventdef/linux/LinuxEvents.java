package de.tum.in.i22.uc.pip.eventdef.linux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.IProcessRelativeName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.cm.datatypes.linux.RemoteSocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SharedFiledescr;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketName;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.pip.distribution.DistributedPipStatus;
import de.tum.in.i22.uc.pip.eventdef.linux.ShutdownEventHandler.Shut;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

/**
 * This class provides functionalities used by multiple events originating from a Linux PEP.
 *
 * @author Florian Kelbert
 *
 */
public abstract class LinuxEvents extends AbstractScopeEventHandler {

	protected static final Logger _logger = LoggerFactory.getLogger(LinuxEvents.class);

	/*
	 *	TODO: Remember man 2 open, fcntl, accept, socket, pipe, dup, socketpair:
	 *some file descriptors close automatically on execve()
	 */

	static String toRealPath(String file) {
		return toRealPath(file, "");
	}

	private static String toRealPath(String dir, String file) {
		Path p = new File(dir, new File(file).getName()).toPath();
		try {
			return p.toRealPath().toString();
		} catch (IOException e) {
			return p.toAbsolutePath().toString();
		}
	}


	void exit(String host, int pid) {

		/**
		 * TODO: There is no exit() or exit_group() system call if a process
		 * gets killed. Therefore, the resources would not be freed.
		 */

		ProcessContainer procCont = (ProcessContainer) _informationFlowModel.getContainer(ProcessName.create(host, pid));
		if (procCont == null) {
			return;
		}

		_informationFlowModel.emptyContainer(procCont);
		_informationFlowModel.removeAllAliasesFrom(procCont);
		_informationFlowModel.removeAllAliasesTo(procCont);
		_informationFlowModel.remove(procCont);

		for (IProcessRelativeName nm : getAllProcessRelativeNames(procCont.getPid())) {
			close(nm);
		}

		SharedFiledescr.unshare(pid);
	}


	void close(IName name) {
		IContainer cont = _informationFlowModel.getContainer(name);

		_informationFlowModel.removeName(name);

		if (cont instanceof SocketContainer) {
			if (_informationFlowModel.getAllNames(cont, FiledescrName.class).size() == 0) {
				shutdownSocket((SocketContainer) cont, Shut.RDWR);
			}
		}
	}

	void shutdownSocket(SocketContainer cont, Shut how) {
		List<SocketName> allSocketNames = _informationFlowModel.getAllNames(cont, SocketName.class);

		if (how == Shut.RD || how == Shut.RDWR) {
			// disallow reception
			_informationFlowModel.emptyContainer(cont);
			_informationFlowModel.removeAllAliasesTo(cont);
		}

		if (how == Shut.WR || how == Shut.RDWR) {
			// disallow transmission

			// we remove all SocketNames for all aliased containers
			for (IContainer aliased : _informationFlowModel.getAliasesFrom(cont)) {
				for (IName name : _informationFlowModel.getAllNames(aliased, SocketName.class)) {
					_informationFlowModel.removeName(name);
				}
			}
			_informationFlowModel.removeAllAliasesFrom(cont);
		}

		if (how == Shut.RDWR) {
			// disallow transmission and reception,
			// therefore delete all socket identifiers
			for (SocketName n : allSocketNames) {
				_informationFlowModel.removeName(n);
			}
		}

		for (SocketName n : allSocketNames) {
			IContainer remoteContainer = _informationFlowModel.getContainer(n);
			if (remoteContainer instanceof RemoteSocketContainer) {
				notifyRemoteShutdown((RemoteSocketContainer) remoteContainer, how);
			}
		}
	}

	private static void notifyRemoteShutdown(RemoteSocketContainer remoteContainer, Shut how) {
		SocketName remoteName = remoteContainer.getSocketName();

		Map<String,String> params = new HashMap<String,String>();

		params.put(EventBasic.PEP_PARAMETER_KEY, "Linux");
		params.put("localIP", remoteName.getRemoteIP());
		params.put("localPort", String.valueOf(remoteName.getRemotePort()));
		params.put("remoteIP", remoteName.getLocalIP());
		params.put("remotePort", String.valueOf(remoteName.getLocalPort()));

		params.put("how", how.toString());

		// TODO
//		distributedPipManager.update(remoteContainer.getLocation(),
//				new EventBasic("Shutdown", params, true));
	}


	IStatus copyDataTransitive(IContainer srcCont, IContainer dstCont) {
		_logger.debug("CopyDataTransitive(" + srcCont + "," + dstCont + ")");

		if (srcCont == null || dstCont == null) {
			return STATUS_OKAY;
		}

		Set<IData> data = _informationFlowModel.getData(srcCont);
		if (data == null || data.size() == 0) {
			return STATUS_OKAY;
		}

		_logger.debug("Data is " + data);


		RemoteDataFlowInfo remoteDataFlow = null;

		if (srcCont instanceof SocketContainer) {
			/*
			 * We are reading from a socket...
			 */

			Set<IContainer> aliases = _informationFlowModel.getAliasesTo(srcCont);

			switch (aliases.size()) {
				case 0:
					// ... but there are no aliases to that socket, so there is nothing to do
					break;
				case 1:
					// ... there is exactly one alias to it
					IContainer c = aliases.iterator().next();

					if (c instanceof RemoteSocketContainer) {

						/*
						 * There is _incoming_ remote data flow.
						 * We now know that both the local location
						 * and the remote location are aware of the data.
						 *
						 * We update the model, assemble the remote
						 * data flow information object and return it.
						 */

						_informationFlowModel.addDataTransitively(data, dstCont);

						Location localLocation = LocalLocation.getInstance();
						Location remoteLocation = ((RemoteSocketContainer) c).getLocation();

						remoteDataFlow = new RemoteDataFlowInfo(remoteLocation);
						remoteDataFlow.addFlow(localLocation, localLocation, data);

						return new DistributedPipStatus(remoteDataFlow);
					}

					break;
				default:
					_logger.error("There should exist at most one such alias. Something went wrong. Somewhere.");
					return new StatusBasic(EStatus.ERROR, "There should exist at most one such alias. Something went wrong. Somewhere.");
			}
		}


		// copy data into all aliased containers
		for (IContainer c : _informationFlowModel.getAliasTransitiveReflexiveClosure(dstCont)) {
			_informationFlowModel.addData(data, c);

			if (c instanceof RemoteSocketContainer) {

				/*
				 * In case we are copying to a RemoteSocketContainer, we
				 * know that _outgoing_ remote data transfer is happening. Thus,
				 * we assemble the information about which data has flown remotely.
				 */
				if (remoteDataFlow == null) {
					remoteDataFlow = new RemoteDataFlowInfo(LocalLocation.getInstance());
				}

				RemoteSocketContainer rsc = (RemoteSocketContainer) c;
				remoteDataFlow.addFlow(rsc.getLocation(), rsc.getSocketName(), data);
			}
		}

		/*
		 * Finally, check whether remote data flow has happened. If
		 * so, return a corresponding status.
		 */
		if (remoteDataFlow != null && !remoteDataFlow.isEmpty()) {
			return new DistributedPipStatus(remoteDataFlow);
		}

		return STATUS_OKAY;
	}


	List<IProcessRelativeName> getAllProcessRelativeNames(int pid) {
		List<IProcessRelativeName> result = new LinkedList<IProcessRelativeName>();

		for (IName name : _informationFlowModel.getAllNames()) {
			if (name instanceof IProcessRelativeName) {
				IProcessRelativeName pname = (IProcessRelativeName) name;
				if (pname.hasPid(pid)) {
					result.add(pname);
				}
			}
		}

		return result;
	}
}
