package de.tum.in.i22.pip.core.eventdef.Linux;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.ShutdownEventHandler.Shut;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.Linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.FilenameName;
import de.tum.in.i22.uc.cm.datatypes.Linux.IProcessRelativeName;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.SocketName;

/**
 * This class provides functionalities used by multiple events originating from a Linux PEP.
 *
 * @author Florian Kelbert
 *
 */
public class LinuxEvents {

	private final static InformationFlowModel ifModel = InformationFlowModel.getInstance();
	private static final Logger _logger = Logger.getLogger(BaseEventHandler.class);

	/*
	 *	TODO: Remember man 2 open, fcntl, accept, socket, pipe, dup, socketpair:
	 *some file descriptors close automatically on execve()
	 */






	private static String getAbsolutePath(File f) {
		try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			return f.getAbsolutePath();
		}
	}

	/*
	 * TODO CHeck: probably we might need to get the traced processes'
	 * current working directory.
	 */

	static String toAbsoluteFilename(String filename) {
		return getAbsolutePath(new File(filename));
	}


	/**
	 * Used by both open() and openat().
	 * @param host
	 * @param pid
	 * @param newfd
	 * @param dirfd
	 * @param filename
	 * @param at_fdcwd
	 * @param truncate
	 */
	static void open(String host, int pid, int newfd, int dirfd, String filename, boolean at_fdcwd, boolean truncate) {
		IName fdName = FiledescrName.create(host, pid, newfd);
		IName fnName;

		File file = new File(filename);

		if (!file.isAbsolute() && !at_fdcwd) {
			// all this part is for openat()

			IName dirfdName = FiledescrName.create(host, pid, dirfd);

			List<FilenameName> names = ifModel.getAllNames(dirfdName, FilenameName.class);

			// the resulting list should always be of size 1.
			if (names.size() != 1) {
				_logger.error("There was not exactly one filename for " + dirfdName);
			}
			else {
				File path = new File(names.get(0).getFilename());

				String pathStr = LinuxEvents.getAbsolutePath(path);

				if (path.isDirectory()) {
					file = new File(pathStr, filename);
				}
				else {
					file = new File(path.getParent(), filename);
				}
			}
		}

		fnName = FilenameName.create(host, LinuxEvents.getAbsolutePath(file));

		// get the file's container (if present)
		IContainer cont = ifModel.getContainer(fnName);

		if (cont != null) {
			if (truncate) {
				ifModel.emptyContainer(cont);
			}
		}
		else {
			cont = new FileContainer();
			ifModel.addName(fnName, cont);
		}
		ifModel.addName(fdName, cont);
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



	public static List<IName> getAllProcessRelativeNames(int pid) {
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
