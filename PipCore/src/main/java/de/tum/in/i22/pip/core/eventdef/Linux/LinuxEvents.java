package de.tum.in.i22.pip.core.eventdef.Linux;

import java.io.File;
import java.io.IOException;
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
import de.tum.in.i22.uc.cm.datatypes.Linux.MmapContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;
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
	 *	TODO: Remember man 2 open, fcntl, accept, socket, pipe, dup, socketpair: some file descriptors close automatically on execve()
	 */






	static String getAbsolutePath(File f) {
		try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			return f.getAbsolutePath();
		}
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
	static void open(String host, String pid, String newfd, String dirfd, String filename, boolean at_fdcwd, boolean truncate) {
		IName fdName = FiledescrName.create(host, pid, newfd);
		IName fnName;

		File file = new File(filename);

		if (!file.isAbsolute() && !at_fdcwd) {
			// all this part is for openat()

			IName dirfdName = FiledescrName.create(host, pid, dirfd);

			List<IName> names = ifModel.getAllNames(dirfdName, FilenameName.class);

			// the resulting list should always be of size 1.
			if (names.size() != 1) {
				_logger.error("There was not exactly one filename for " + dirfdName);
			}
			else {
				File path = new File(((FilenameName) names.get(0)).getFilename());

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


	static void exit(String host, String pid) {
		IContainer processContainer = ifModel.getContainer(ProcessName.create(host, pid));
		if (processContainer == null) {
			return;
		}

		// all mapped files are unmapped upon process termination, cf. man 2 mmap
		for (IContainer c : ifModel.getAliasesFromContainer(processContainer)) {
			if (c instanceof MmapContainer) {
				ifModel.remove(c);
			}
		}
		for (IContainer c : ifModel.getAliasesTo(processContainer)) {
			if (c instanceof MmapContainer) {
				ifModel.remove(c);
			}
		}

		ifModel.emptyContainer(processContainer);
		ifModel.removeAllAliasesFrom(processContainer);
		ifModel.removeAllAliasesTo(processContainer);
		ifModel.remove(processContainer);

		for (IName nm : ifModel.getAllNamingsFrom(processContainer)) {
			LinuxEvents.close(nm);
		}
	}


	static void close(IName name) {
		if (name instanceof SocketName) {
			LinuxEvents.closeSocket(name);
		}
		InformationFlowModel.getInstance().removeName(name);
	}

	private static void closeSocket(IName name) {
		InformationFlowModel ifModel = InformationFlowModel.getInstance();

		IContainer container = ifModel.getContainer(name);

		int count = 0;

		if (container != null) {
			for (IName n : ifModel.getAllNames(container)) {
				if (!(n instanceof SocketName)) {
					count++;
				}
			}
		}

		if (count == 1) {
			shutdown(name, Shut.SHUT_RDWR);
		}
	}

	static void shutdown(IName name, Shut how) {
		if (name == null || how == null) {
			return;
		}

		IContainer container = ifModel.getContainer(name);

		if (container != null) {
			List<IName> allNames = ifModel.getAllNames(container);

			if (how == Shut.SHUT_RD || how == Shut.SHUT_RDWR) {
				// disallow reception
				ifModel.emptyContainer(container);
				ifModel.removeAllAliasesTo(container);
			}

			if (how == Shut.SHUT_WR || how == Shut.SHUT_RDWR) {
				// disallow transmission
				ifModel.removeAllAliasesFrom(container);
			}

			if (how == Shut.SHUT_RDWR) {
				// disallow transmission and reception,
				// therefore delete all socket identifiers
				for (IName n : allNames) {
					if (n instanceof SocketName) {
						ifModel.removeName(n);
					}
				}
			}

			for (IName n : allNames) {
				if (n instanceof SocketName) {
					// if remote IP is in fact remote, then do a remote call to tell about connection teardown
					if (!(((SocketName) n).getRemoteIP().equals(((SocketName) n).getLocalIP()))) {
						IContainer remoteContainer = ifModel.getContainer(n);

						// TODO remote call
					}
				}
			}
		}
	}
}
