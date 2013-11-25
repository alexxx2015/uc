package de.fraunhofer.iese.ind2uce.pdp;

import java.rmi.RemoteException;

public class LinuxPolicyDecisionPoint extends PolicyDecisionPoint {
	private static final long serialVersionUID = 1L;


	public LinuxPolicyDecisionPoint() throws RemoteException { 
	}
	

	@Override
	public void initialize() throws Exception {
		_logger.info("Loading native PDP library");
		try {
			loadDinamicLibrary("pdpNative/linux", "pdp.dll");
			pdpRunning = true;
			_logger.info("Native PDP library loaded...");
		} catch (Exception e) {
			_logger.error("Could not load native PDP library! " + e.getMessage());
			throw e;
		}
	}

}
