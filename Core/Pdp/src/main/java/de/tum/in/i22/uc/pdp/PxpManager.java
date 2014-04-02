package de.tum.in.i22.uc.pdp;

import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.pdp.core.shared.IPdpExecuteAction;

public class PxpManager {
	private static PxpManager _instance;

	public static PxpManager getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (_instance == null) {
			synchronized (PxpManager.class) {
				if (_instance == null) _instance = new PxpManager();
			}
		}
		return _instance;
	}

	public void execute(IPdpExecuteAction execAction) {
		// TODO Auto-generated method stub

	}

	public boolean registerPxp(IPxpSpec pxp) {
		// TODO Auto-generated method stub
		return false;
	}
}
