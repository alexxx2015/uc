package de.fraunhofer.iese.ind2uce.pdp;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.fraunhofer.iese.ind2uce.internal.pdp.Decision;
import de.fraunhofer.iese.ind2uce.internal.pdp.Event;
import de.fraunhofer.iese.ind2uce.internal.pdp.IPolicyDecisionPoint;

public class PolicyDecisionPoint extends UnicastRemoteObject implements IPolicyDecisionPoint, Serializable {
	private static final long serialVersionUID = -6823961095919408237L;
	private static Logger _logger = Logger.getLogger(PolicyDecisionPoint.class);

	public static IPolicyDecisionPoint curInstance = null;
	public static boolean pdpRunning = false;

	private PolicyDecisionPoint() throws RemoteException {
		_logger.info("Loading native PDP library");
		try {
			loadDinamicLibrary("win64", "libpthread-2.dll");
			loadDinamicLibrary("win64", "libgnurx-0.dll");
			loadDinamicLibrary("win64", "pdp.dll");
			pdpRunning = true;
			_logger.info("Native PDP library loaded...");
		} catch (Exception e) {
			System.out.println("Could not load native PDP library!");
			System.out.println(e.getMessage());
		}
	}

	private void loadDinamicLibrary(String directory, String dllName) throws Exception {
		String relativePath = null;
		if (directory != null) {
			relativePath = "/" + directory + "/" + dllName;
		} else {
			relativePath = dllName;
		}
		
		URL dll = PolicyDecisionPoint.class.getResource(relativePath);
		File dllFile = new File(dll.toURI());
		System.load(dllFile.getAbsolutePath());
	}

	public static IPolicyDecisionPoint getInstance() throws RemoteException {
		if (curInstance == null)
			curInstance = new PolicyDecisionPoint();
		_logger.info("Returning curinstance of PDP...");
		return curInstance;
	}

	// Native method declaration
	public native int pdpStart();

	public native int pdpStop();

	public native int registerPEP(String pepName, String className);

	public native int registerAction(String actionName, String pepName);

	public native int registerPXP(String pepName, String className);

	public native int registerPXPinstance(String pxpName, Object clazz);

	public native int registerExecutor(String actionName, String pepName);

	public native String pdpNotifyEventXML(String event);

	public native Decision pdpNotifyEventJNI(Event event);

	public native int pdpDeployPolicy(String mechanism_doc_path);

	public native int pdpDeployPolicyString(String policy, String namespace);

	public native int pdpDeployMechanism(String mechanism_doc_path, String mechName);

	public native int pdpDeployMechanismString(String policy, String mechName);

	public native int pdpRevokeMechanism(String mechName, String namespace);

	public native String listDeployedMechanisms();

	public native ArrayList<String> listDeployedMechanismsJNI();

	public native int setRuntimeLogLevel(int newLevel);

}
