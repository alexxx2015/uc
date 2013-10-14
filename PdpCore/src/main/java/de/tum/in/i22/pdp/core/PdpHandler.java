package de.tum.in.i22.pdp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;
import de.fraunhofer.iese.ind2uce.internal.pdp.AuthorizationAction;
import de.fraunhofer.iese.ind2uce.internal.pdp.Decision;
import de.fraunhofer.iese.ind2uce.internal.pdp.Event;
import de.fraunhofer.iese.ind2uce.internal.pdp.ExecuteAction;
import de.fraunhofer.iese.ind2uce.internal.pdp.Param;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
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
	
	private static final String TIMESTAMP = "timestamp";

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
		
		_logger.debug("Prapere to invoke old PDP");
		// convert IEvent to de.fraunhofer.iese.ind2uce.Event
		Event curEvent = new Event(event.getName(), !event.isActual());
		
		Map<String, String> map = event.getParameters();
		
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			curEvent.addStringParameter(key, map.get(key));
		}
		
		curEvent.addLongParameter(TIMESTAMP, event.getTimestamp());
		
		Decision d = this.pdpNotifyEventJNI(curEvent);
		_logger.debug("Response from the old PDP: " + d);
		
		_logger.debug("Convert decision to response");
		AuthorizationAction authorizationAction = d.getAuthorizationAction();
		
		_logger.debug("Create status");
		IStatus status = convertFrom(authorizationAction);
		
		_logger.debug("Create modified event");
		EventBasic modifiedEvent = new EventBasic();
		modifiedEvent.setName(event.getName());
		// only attempted event can be modified
		modifiedEvent.setActual(false);
		if (status.getEStatus() == EStatus.MODIFY) {
			List<Param<?>> list = authorizationAction.getModifiers();
			for (Param<?> param : list) {
				String paramName = param.getName();
				if (paramName.equals(TIMESTAMP)) {
					try {
						long timestamp = (Long)param.getValue();
						modifiedEvent.setTimestamp(timestamp);
					} catch (Exception e){
						_logger.error("Error parsing timestamp.", e);
					}
				} else {
					try {
						modifiedEvent.addParameter(paramName, (String)param.getValue());
					} catch (Exception e) {
						_logger.error("Error parsing parameter " + paramName, e);
					}
				}
			}
		}
		
		
		_logger.debug("Create execute actions");
		List<IEvent> executeActions = new ArrayList<>();
		List<ExecuteAction> list = authorizationAction.getExecuteActions();
		long timestamp = System.currentTimeMillis();
		for (ExecuteAction executeAction : list ){
			// for each element in the list create corresponding event object
			EventBasic executeEvent = new EventBasic();
			executeEvent.setName(executeAction.getName());
			executeEvent.setActual(false);
			executeEvent.setTimestamp(timestamp);
			
			List<Param<?>> parameters = executeAction.getParams();
			for (Param<?> param : parameters) {
				String paramName = param.getName();
				try {
					executeEvent.addParameter(paramName, (String)param.getValue());
				} catch (Exception e) {
					_logger.error("Error parsing parameter " + paramName, e);
				}
			}
			
			executeActions.add(executeEvent);
		}
		
		ResponseBasic response = new ResponseBasic(status, executeActions, modifiedEvent);
		
		_logger.debug("Response to return: " + response);
		
		return response;
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
	
	private IStatus convertFrom(AuthorizationAction action) {
		StatusBasic status = new StatusBasic();
		if (!action.getAuthorizationAction()) {
			status.seteStatus(EStatus.INHIBIT);
		} else {
			List<Param<?>> list = action.getModifiers();
			if (list == null || list.isEmpty()) {
				status.seteStatus(EStatus.ALLOW);
			} else {
				status.seteStatus(EStatus.MODIFY);
			}
		}
		
		return status;
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
