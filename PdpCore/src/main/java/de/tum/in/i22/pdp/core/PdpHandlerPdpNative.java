package de.tum.in.i22.pdp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;

import com.google.inject.Inject;

import de.fraunhofer.iese.pef.pdp.PolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.AuthorizationAction;
import de.fraunhofer.iese.pef.pdp.internal.Decision;
import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.ExecuteAction;
import de.fraunhofer.iese.pef.pdp.internal.Param;
import de.tum.in.i22.cm.pdp.internal.Mechanism;
import de.tum.in.i22.pdp.pipcacher.IPdpCore2PipCacher;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
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
 * PdpHandler
 * 
 * @author Stoimenov
 * This Handler invokes the old PDP that is written in c.
 */
public class PdpHandlerPdpNative implements IIncoming {

	private static final Logger _logger = Logger.getLogger(PdpHandlerPdpNative.class);
	public static boolean pdpRunning = false;

	private static PolicyDecisionPoint _lpdp;

	@Inject
	public PdpHandlerPdpNative(PolicyDecisionPoint lpdp) {
		_lpdp = lpdp;
		try {
			_logger.info("Get instance of native PDP ...");
			_lpdp.initialize();
			_logger.info("Start native PDP ..");
			_lpdp.pdpStart();
			_logger.info("Native PDP started");
			_lpdp.pdpDeployPolicy("/home/alex/Policies/DontSendSmartMeterData.xml");
			_logger.info("Test policy deployed");
		} catch (Exception e) {
			_logger.fatal("Could not load native PDP library! " + e.getMessage());
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
		Event curEvent = new Event(event.getPrefixedName(), !event.isActual(),
				event.getTimestamp());

		Map<String, String> map = event.getParameters();

		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			curEvent.addStringParameter(key, map.get(key));
		}

		Decision d = null;
//		try {
			d = _lpdp.pdpNotifyEventJNI(curEvent);
//		} catch (RemoteException e) {
//			_logger.error("Call to native lib failed.", e);
//		}
		_logger.debug("Response from the old PDP: " + d);

		_logger.debug("Convert decision to response");
		AuthorizationAction authorizationAction = d.getAuthorizationAction();

		_logger.debug("Create status");
		IStatus status = convertFrom(authorizationAction);

		_logger.debug("Create modified event");
		EventBasic modifiedEvent = new EventBasic();
		modifiedEvent.setName(event.getPrefixedName());
		// only attempted event can be modified
		modifiedEvent.setActual(false);
		modifiedEvent.setTimestamp(event.getTimestamp());
		if (status.getEStatus() == EStatus.MODIFY) {
			List<Param<?>> list = authorizationAction.getModifiers();
			for (Param<?> param : list) {
				String paramName = param.getName();
				
				try {
					modifiedEvent.addParameter(paramName,
							(String) param.getValue());
				} catch (Exception e) {
					_logger.error("Error parsing parameter " + paramName, e);
				}

			}
		}

		_logger.debug("Create execute actions");
		List<IEvent> executeActions = new ArrayList<>();
		List<ExecuteAction> list = authorizationAction.getExecuteActions();
		long timestamp = System.currentTimeMillis();
		for (ExecuteAction executeAction : list) {
			// for each element in the list create corresponding event object
			EventBasic executeEvent = new EventBasic();
			executeEvent.setName(executeAction.getName());
			executeEvent.setActual(false);
			executeEvent.setTimestamp(timestamp);

			List<Param<?>> parameters = executeAction.getParams();
			for (Param<?> param : parameters) {
				String paramName = param.getName();
				try {
					executeEvent.addParameter(paramName,
							(String) param.getValue());
				} catch (Exception e) {
					_logger.error("Error parsing parameter " + paramName, e);
				}
			}

			executeActions.add(executeEvent);
		}

		ResponseBasic response = new ResponseBasic(status, executeActions,
				modifiedEvent);

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

	@Override
	public IStatus setPdpCore2PipCacher(IPdpCore2PipCacher core2cacher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus setPdpEngine2PipCacher(IPdpEngine2PipCacher engine2cacher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicy(String policyFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ArrayList<Mechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}

}
