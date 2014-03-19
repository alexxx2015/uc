package de.tum.in.i22.pip.core.eventdef.Linux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.Linux.ShutdownEventHandler.Shut;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.IProcessRelativeName;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;
import de.tum.in.i22.uc.cm.datatypes.Linux.RemoteSocketContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketName;

/**
 * This class provides functionalities used by multiple events originating from a Linux PEP.
 *
 * @author Florian Kelbert
 *
 */
class LinuxEvents {

	private static final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();

	private static final InformationFlowModel ifModel = InformationFlowModel.getInstance();

	private final static IStatus STATUS_OKAY = _messageFactory.createStatus(EStatus.OKAY);
	private final static IStatus STATUS_ERROR = _messageFactory.createStatus(EStatus.ERROR);

	/*
	 *	TODO: Remember man 2 open, fcntl, accept, socket, pipe, dup, socketpair:
	 *some file descriptors close automatically on execve()
	 */


	static String toRealPath(String file) {
		return toRealPath(file, "");
	}

	static String toRealPath(String dir, String file) {
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
				shutdownSocket((SocketContainer) cont, Shut.SHUT_RDWR);
			}
		}
	}

	static void shutdownSocket(SocketContainer cont, Shut how) {
		List<SocketName> allSocketNames = ifModel.getAllNames(cont, SocketName.class);

		if (how == Shut.SHUT_RD || how == Shut.SHUT_RDWR) {
			// disallow reception
			ifModel.emptyContainer(cont);
			ifModel.removeAllAliasesTo(cont);
		}

		if (how == Shut.SHUT_WR || how == Shut.SHUT_RDWR) {
			// disallow transmission
			ifModel.removeAllAliasesFrom(cont);
		}

		if (how == Shut.SHUT_RDWR) {
			// disallow transmission and reception,
			// therefore delete all socket identifiers
			for (SocketName n : allSocketNames) {
				ifModel.removeName(n);
			}
		}

		for (SocketName n : allSocketNames) {
			// if remote IP is in fact remote, then do a remote call to tell about connection teardown
			if (!(n.getRemoteIP().equals(n.getLocalIP()))) {
				IContainer remoteContainer = ifModel.getContainer(n);

				// TODO remote call
			}
		}
	}


	static IStatus copyDataTransitive(IContainer srcCont, IContainer dstCont) {
		if (srcCont == null || dstCont == null) {
			return STATUS_OKAY;
		}

		Set<IData> data = ifModel.getDataInContainer(srcCont);
		if (data == null || data.size() == 0) {
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		// copy into all containers aliased from the destination container
		for (IContainer c : ifModel.getAliasTransitiveClosure(dstCont)) {
			if (c instanceof RemoteSocketContainer) {
				System.out.println("Remote data transfer to " + c);
			}
			else {
				ifModel.addDataToContainerMappings(data, c);
			}
		}

		// now, also copy into the actual (direct) destination container ...
		// ... but only if it is not a socket.
		if (!(dstCont instanceof SocketContainer)) {
			ifModel.addDataToContainerMappings(data, dstCont);
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
//			else if (name instanceof SharedFiledescrName) {
//				SharedFiledescrName sname = (SharedFiledescrName) name;
//				if (sname.isSharedWith(pid)) {
//					result.add(sname);
//				}
//			}
		}

		return result;
	}
}
