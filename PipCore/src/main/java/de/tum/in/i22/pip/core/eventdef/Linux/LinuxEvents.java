package de.tum.in.i22.pip.core.eventdef.Linux;

import java.util.List;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;

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

	static void shutdown(IName name, Shut mode) {
		InformationFlowModel ifModel = InformationFlowModel.getInstance();

		IContainer container = ifModel.getContainer(name);

		if (container != null) {
			List<IName> allNames = ifModel.getAllNames(container);

			if (mode == Shut.SHUT_RD || mode == Shut.SHUT_RDWR) {
				// disallow reception
				ifModel.emptyContainer(container);
				ifModel.removeAllAliasesTo(container);
			}

			if (mode == Shut.SHUT_WR || mode == Shut.SHUT_RDWR) {
				// disallow transmission
				ifModel.removeAllAliasesFrom(container);
			}

			if (mode == Shut.SHUT_RDWR) {
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
						// TODO
					}
				}
			}
		}
	}
}
