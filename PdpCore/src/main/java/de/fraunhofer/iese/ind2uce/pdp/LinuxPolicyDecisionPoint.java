package de.fraunhofer.iese.ind2uce.pdp;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

public class LinuxPolicyDecisionPoint extends PolicyDecisionPoint {
	private static final long serialVersionUID = 1L;


	public LinuxPolicyDecisionPoint() throws RemoteException { 
	}
	

	@Override
	public void initialize() throws Exception {
		_logger.info("Loading native PDP library");
		try {
//			System.loadLibrary("/home/uc/pdp/PdpCore/src/main/resources/pdpNative/linux/pdp.jar");
//			loadDinamicLibrary("pdpNative/linux","pdp.jar");
			pdpRunning = true;
			_logger.info("Native PDP library loaded...");
		} catch (Exception e) {
			_logger.error("Could not load native PDP library! " + e.getMessage());
			throw e;
		}
	}
	
	protected void loadDinamicLibrary(String directory, String dllName) throws Exception {
		String relativePath = null;
		if (directory != null) {
			relativePath = "/" + directory + "/" + dllName;
		} else {
			relativePath = "/" + dllName;
		}
		
		URL dll = PolicyDecisionPoint.class.getResource(relativePath);
		_logger.info("Loading: " + dll.toURI());
		File dllFile = new File(dll.toURI());
		System.loadLibrary(dllFile.getAbsolutePath());
	}
	

}
