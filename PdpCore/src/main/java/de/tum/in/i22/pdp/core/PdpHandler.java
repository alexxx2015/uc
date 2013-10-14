package de.tum.in.i22.pdp.core;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;
import de.fraunhofer.iese.ind2uce.internal.pdp.Decision;
import de.fraunhofer.iese.ind2uce.internal.pdp.Event;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * This is just a stub for now
 * 
 * @author Stoimenov
 * 
 */
public class PdpHandler implements IIncoming {

	private static final Logger _logger = Logger.getLogger(PdpHandler.class);

	private static PdpHandler _instance;
	public static boolean pdpRunning = false;

	public static PdpHandler getInstance() {
		if (_instance == null) {
			_instance = new PdpHandler();
		}
		return _instance;
	}

	private PdpHandler() {
		System.out.println("Loading native PDP library");

		try {
			System.loadLibrary("pdp");
			pdpRunning = true;
			System.out.println("Native PDP library loaded...");
			this.pdpStart();
			System.out.println("native PDP started...");
		} catch (Exception e) {
			System.out.println("Could not load native PDP library!");
			System.out.println(e.getMessage());
		} 
	}

	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO implement
		_logger.debug("Deploy mechanism called");
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO implement
		_logger.debug("Export mechanism called");
		return DummyMessageGen.createMechanism();
	}

	@Override
	public IStatus revokeMechanism(String par) {
		_logger.debug("Revoke mechanism called");
		// TODO implement
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		// TODO implement
		_logger.debug("Notify event called");
	
		
		// convert IEvent to de.fraunhofer.iese.ind2uce.Event
		Event curEvent = new Event(event.getName(), event.isActual());
		
		Map<String, String> map = event.getParameters();
		
		Decision d = this.pdpNotifyEventJNI(curEvent);
		
		// convert Decision to IResponse...
		
		return DummyMessageGen.createResponse();
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			byte[] jarFileBytes,
			EConflictResolution flagForTheConflictResolution) {
		// leave empty
		// this method is never called
		// instead PDP delegates it to PIP
		return null;
	}
	
	  // Native method declaration
	  public native int               pdpStart();
	  public native int               pdpStop();
	  
	  public native int               registerPEP(String pepName, String className);
	  public native int               registerAction(String actionName, String pepName);
	  public native int               registerPXP(String pepName, String className);
	  public native int               registerPXPinstance(String pxpName, Object clazz);
	  public native int               registerExecutor(String actionName, String pepName);
	  
	  public native String            pdpNotifyEventXML(String event);
	  public native Decision          pdpNotifyEventJNI(Event event);
	  
	  public native int               pdpDeployPolicy(String mechanism_doc_path);
	  public native int               pdpDeployPolicyString(String policy, String namespace);
	  public native int               pdpDeployMechanism(String mechanism_doc_path, String mechName);
	  public native int               pdpDeployMechanismString(String policy, String mechName);
	  public native int               pdpRevokeMechanism(String mechName, String namespace);

	  public native String            listDeployedMechanisms();
	  public native ArrayList<String> listDeployedMechanismsJNI();

	  public native int               setRuntimeLogLevel(int newLevel);
}
