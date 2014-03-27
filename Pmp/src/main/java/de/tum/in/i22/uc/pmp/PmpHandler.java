package de.tum.in.i22.uc.pmp;

import de.tum.in.i22.uc.cm.requests.GenericPmpHandler;

public class PmpHandler extends GenericPmpHandler {
	private static PmpHandler _instance;

	private PmpHandler() {
	}

	public static PmpHandler getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (_instance == null) {
			synchronized (PmpHandler.class) {
				if (_instance == null) _instance = new PmpHandler();
			}
		}
		return _instance;
	}
}
