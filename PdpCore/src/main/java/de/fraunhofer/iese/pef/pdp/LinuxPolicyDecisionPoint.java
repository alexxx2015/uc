package de.fraunhofer.iese.pef.pdp;

import java.rmi.RemoteException;
import java.util.ArrayList;

import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.Decision;
import de.fraunhofer.iese.pef.pdp.internal.Event;

public class LinuxPolicyDecisionPoint extends PolicyDecisionPoint {
	private static final long serialVersionUID = 1L;

	private static IPolicyDecisionPoint _instance = null;
	public LinuxPolicyDecisionPoint() throws RemoteException { 
	}
	
	public static IPolicyDecisionPoint  getInstance()
	{
			try {
				if(_instance==null)
					_instance=new LinuxPolicyDecisionPoint();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return _instance;
	}

	@Override
	public void initialize() throws Exception {
		_logger.info("Loading native PDP library");
		try {
			System.load("/home/uc/xlayerpip/jpdp/bin/linux/components/pdp/libpdp.so");
			pdpRunning = true;
			_logger.info("Native PDP library loaded...");
			_logger.info("starting returned: " + this.pdpStart());
			int ret=this.pdpDeployPolicy("/home/uc/xlayerpip/jpdp/src/main/xml/examples/testTUM.xml");
			_logger.info("policy should be deployed... "+ret);
//			_logger.info("mechanisms: " + this.listDeployedMechanismsJNI());
//			Event e = new Event("testEvent", true);
//			e.addStringParameter("val1", "value1");
//			e.addStringParameter("val2", "value2");
//			Decision d = this.pdpNotifyEventJNI(e);
//			_logger.info("decision: " + d);
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
	
	
	// Native method declaration
//	public native int pdpStart();

//	public native int pdpStop();
//
//	public native int registerPEP(String pepName, String className);
//
//	public native int registerAction(String actionName, String pepName);
//
//	public native int registerPXP(String pepName, String className);
//
//	public native int registerPXPinstance(String pxpName, Object clazz);
//
//	public native int registerExecutor(String actionName, String pepName);
//
//	public native String pdpNotifyEventXML(String event);
//
//	public native Decision pdpNotifyEventJNI(Event event);
//
//	public native int pdpDeployPolicy(String mechanism_doc_path);
//
//	public native int pdpDeployPolicyString(String policy, String namespace);
//
//	public native int pdpDeployMechanism(String mechanism_doc_path, String mechName);
//
//	public native int pdpDeployMechanismString(String policy, String mechName);
//
//	public native int pdpRevokeMechanism(String mechName, String namespace);
//
//	public native String listDeployedMechanisms();
//
//	public native ArrayList<String> listDeployedMechanismsJNI();
//
//	public native int setRuntimeLogLevel(int newLevel);
//
//	@Override
//	public native int registerPEP(String pepName, String className, String methodName,
//			String methodSignature);
//
//	@Override
//	public native int registerPXP(String pepName, String className, String methodName,
//			String methodSignature);
//
//	@Override
//	public native int pdpDeployPolicyString(String policy);

}
