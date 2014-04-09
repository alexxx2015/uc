package de.tum.in.i22.uc.pip.eventdef.linux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.IProcessRelativeName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.cm.datatypes.linux.RemoteSocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketName;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.eventdef.linux.ShutdownEventHandler.Shut;
import de.tum.in.i22.uc.pip.extensions.distribution.DistributedPipStatus;

/**
 * This class provides functionalities used by multiple events originating from a Linux PEP.
 *
 * @author Florian Kelbert
 *
 */
public class LinuxEvents {

	protected static final Logger _logger = LoggerFactory.getLogger(LinuxEvents.class);

	private static final IMessageFactory messageFactory = MessageFactoryCreator.createMessageFactory();

	private static final BasicInformationFlowModel ifModel = InformationFlowModelManager.getInstance().getBasicInformationFlowModel();

	private static final IStatus STATUS_OKAY = messageFactory.createStatus(EStatus.OKAY);

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


	static void exit(String host, int pid) {
		ProcessContainer procCont = (ProcessContainer) ifModel.getContainer(ProcessName.create(host, pid));
		if (procCont == null) {
			return;
		}

		ifModel.emptyContainer(procCont);
		ifModel.removeAllAliasesFrom(procCont);
		ifModel.removeAllAliasesTo(procCont);
		ifModel.remove(procCont);

		for (IName nm : getAllProcessRelativeNames(procCont.getPid())) {
			LinuxEvents.close(nm);
		}
	}


	static void close(IName name) {
		IContainer cont = ifModel.getContainer(name);

		ifModel.removeName(name);

		if (cont instanceof SocketContainer) {
			if (ifModel.getAllNames(cont, FiledescrName.class).size() == 0) {
				shutdownSocket((SocketContainer) cont, Shut.RDWR);
			}
		}
	}

	static void shutdownSocket(SocketContainer cont, Shut how) {
		List<SocketName> allSocketNames = ifModel.getAllNames(cont, SocketName.class);

		if (how == Shut.RD || how == Shut.RDWR) {
			// disallow reception
			ifModel.emptyContainer(cont);
			ifModel.removeAllAliasesTo(cont);
		}

		if (how == Shut.WR || how == Shut.RDWR) {
			// disallow transmission

			// we remove all SocketNames for all aliased containers
			for (IContainer aliased : ifModel.getAliasesFrom(cont)) {
				for (IName name : ifModel.getAllNames(aliased, SocketName.class)) {
					ifModel.removeName(name);
				}
			}
			ifModel.removeAllAliasesFrom(cont);
		}

		if (how == Shut.RDWR) {
			// disallow transmission and reception,
			// therefore delete all socket identifiers
			for (SocketName n : allSocketNames) {
				ifModel.removeName(n);
			}
		}

		for (SocketName n : allSocketNames) {
			IContainer remoteContainer = ifModel.getContainer(n);
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


	static IStatus copyDataTransitive(IContainer srcCont, IContainer dstCont) {
		_logger.debug("CopyDataTransitive(" + srcCont + "," + dstCont + ")");

		if (srcCont == null || dstCont == null) {
			return STATUS_OKAY;
		}

		Set<IData> data = ifModel.getDataInContainer(srcCont);
		if (data == null || data.size() == 0) {
			return messageFactory.createStatus(EStatus.OKAY);
		}

		_logger.debug("Data is " + data);

		// this map will remember the remote data flows that have occurred.
		Map<Location,Map<IName,Set<IData>>> remoteDataFlows = new HashMap<>();

		// copy into all containers aliased from the destination container
		for (IContainer c : ifModel.getAliasTransitiveClosure(dstCont)) {

			/*
			 * In case we are copying to a RemoteSocketContainer, we
			 * know that a remote data transfer is happening. Thus,
			 * we assemble the information which data has flown remotely.
			 */
			if (c instanceof RemoteSocketContainer) {
				RemoteSocketContainer rsc = (RemoteSocketContainer) c;
				_logger.debug("Preparing to copy data " + data + " to container " + c);

				Map<IName,Set<IData>> map = remoteDataFlows.get(rsc.getLocation());
				if (map == null) {
					map = new HashMap<>();
					remoteDataFlows.put(rsc.getLocation(), map);
				}
				map.put(rsc.getSocketName(), data);

			}
			else {
				// this is regular, local, data flow
				ifModel.addDataToContainer(data, c);
			}
		}

		/*
		 * Now, also copy into the actual (direct) destination container ...
		 * ... but only if it is not a socket.
		 */
		if (!(dstCont instanceof SocketContainer)) {
			ifModel.addDataToContainer(data, dstCont);
		}


		/*
		 * Finally, check whether remote data flow has happened. If
		 * so, return a corresponding status.
		 */
		if (remoteDataFlows.size() > 0) {
			return DistributedPipStatus.createRemoteDataFlowStatus(remoteDataFlows);
		}

		return STATUS_OKAY;
	}


	static List<IName> getAllProcessRelativeNames(int pid) {
		List<IName> result = new ArrayList<IName>();

		for (IName name : ifModel.getAllNames()) {
			if (name instanceof IProcessRelativeName) {
				IProcessRelativeName pname = (IProcessRelativeName) name;
				if (pname.getPid() == pid) {
					result.add(pname);
				}
			}
		}

		return result;
	}
}
