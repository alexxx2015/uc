package de.fraunhofer.iese.pef.pdp;

import java.rmi.RemoteException;


public class Win64PolicyDecisionPoint extends PolicyDecisionPoint {
	private static final long serialVersionUID = 1L;

	public Win64PolicyDecisionPoint() throws RemoteException { 
	}
	

	@Override
	public void initialize() throws Exception {
		_logger.info("Loading native PDP library");
		try {
			loadDinamicLibrary("pdpNative/win64", "libiconv-2.dll");
			loadDinamicLibrary("pdpNative/win64", "libintl-8.dll");
			loadDinamicLibrary("pdpNative/win64", "libglib-2.0-0.dll");
			loadDinamicLibrary("pdpNative/win64", "pdp.dll");
			pdpRunning = true;
			_logger.info("Native PDP library loaded...");
		} catch (Exception e) {
			_logger.error("Could not load native PDP library! " + e.getMessage());
			throw e;
		}
	}
	
	@Override
	public int registerPEP(String pepName, String className, String methodName,
			String methodSignature) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int registerPXP(String pepName, String className, String methodName,
			String methodSignature) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int pdpDeployPolicyString(String policy) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

}
