package de.tum.in.i22.uc.pmp;

import de.tum.in.i22.uc.cm.requests.GenericPmpHandler;

public class PmpHandler extends GenericPmpHandler {
	private static PmpHandler _instance;

	private PmpHandler(){
	}

	public static synchronized PmpHandler getInstance(){
		if (_instance == null) {
			_instance = new PmpHandler();
		}
		return _instance;
	}
}
