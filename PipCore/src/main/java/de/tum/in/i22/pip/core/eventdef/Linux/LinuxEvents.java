package de.tum.in.i22.pip.core.eventdef.Linux;

import java.util.List;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;

/**
 * This class provides functionalities used by multiple events originating from a Linux PEP.
 *
 * @author Florian Kelbert
 *
 */
public class LinuxEvents {

	/* TODO: Implement
	 * Calls to remote PIP in accept() and shutdown()
	 * fork()
	 * read()
	 * write()
	 * open()
	 * unlink()
	 * kill()
	 * dup()
	 * execve()
	 * rename()
	 * mmap()
	 * truncate()
	 */



	static enum Shut {
		SHUT_RDWR,
		SHUT_RD,
		SHUT_WR
	}



	static void close(Name name) {
		if (name instanceof SocketName) {
			LinuxEvents.closeSocket(name);
		}
		InformationFlowModel.getInstance().removeName(name);
	}

	private static void closeSocket(Name name) {
		InformationFlowModel ifModel = InformationFlowModel.getInstance();

		String containerId = ifModel.getContainerIdByName(name);

		int count = 0;

		if (containerId != null) {
			for (Name n : ifModel.getAllNames(containerId)) {
				if (!(n instanceof SocketName)) {
					count++;
				}
			}
		}

		if (count == 1) {
			shutdown(name, Shut.SHUT_RDWR);
		}
	}

	static void shutdown(Name name, Shut mode) {
		InformationFlowModel ifModel = InformationFlowModel.getInstance();

		String containerId = ifModel.getContainerIdByName(name);

		if (containerId != null) {
			List<Name> allNames = ifModel.getAllNames(containerId);

			if (mode == Shut.SHUT_RD || mode == Shut.SHUT_RDWR) {
				// disallow reception
				ifModel.emptyContainer(containerId);
				ifModel.removeAllAliasesTo(containerId);
			}

			if (mode == Shut.SHUT_WR || mode == Shut.SHUT_RDWR) {
				// disallow transmission
				ifModel.removeAllAliasesFrom(containerId);
			}

			if (mode == Shut.SHUT_RDWR) {
				// disallow transmission and reception,
				// therefore delete all socket identifiers
				for (Name n : allNames) {
					if (n instanceof SocketName) {
						ifModel.removeName(n);
					}
				}
			}

			for (Name n : allNames) {
				if (n instanceof SocketName) {
					// if remote IP is in fact remote, then do a remote call to tell about connection teardown
					if (!(((SocketName) n).getRemoteIP().equals(((SocketName) n).getLocalIP()))) {
						// TODO
					}
				}
			}
		}
	}
}
