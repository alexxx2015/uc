package de.tum.in.i22.uc.pdp.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pdp.PxpManager;
import de.tum.in.i22.uc.pdp.core.AuthorizationAction.Authorization;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

/**
 * Decision is the object produced by the PDP as a result of an event. It
 * contains information about permissiveness of the event and desired actions to
 * be performed.
 */
public class Decision implements java.io.Serializable {
	private static Logger _logger = LoggerFactory.getLogger(Decision.class);

	private static final long serialVersionUID = 4922446035665121547L;

	private final AuthorizationAction _authorizationAction;

	/** 'optional' executeActions processed by PXP */
	private final List<ExecuteAction> _executeActions = new LinkedList<>();
	private PxpManager _pxpManager;

	public Decision(AuthorizationAction authAction, PxpManager pxpManager) {
		_authorizationAction = authAction;
		_pxpManager = pxpManager;
	}

	public void processMechanism(Mechanism mech, IEvent curEvent) {
		_logger.debug("Processing mechanism={} for decision", mech.getName());

		AuthorizationAction curAuthAction = mech.getAuthorizationAction();
		if (_authorizationAction.getAuthorization() == Authorization.ALLOW) {
			_logger.debug("Decision still allowing event, processing mechanisms authActions");

			do {
				_logger.debug("Processing authorizationAction {}", curAuthAction.getName());
				if (curAuthAction.getAuthorization() == Authorization.ALLOW) {
					_logger.debug("Executing specified executeActions: {}", curAuthAction.getExecuteActions().size());
					boolean executionSuccess = true;

					for (ExecuteAction execAction : curAuthAction.getExecuteActions()) {
						_logger.debug("Executing [{}]", execAction.getName());

						executionSuccess &= _pxpManager.execute(execAction, true);
					}

					if (!executionSuccess) {
						_logger.warn("Execution failed; continuing with fallback authorization action (if present)");
						curAuthAction = curAuthAction.getFallback();
						if (curAuthAction == null) {
							_logger.warn("No fallback present; implicit INHIBIT");
							_authorizationAction.setAuthorization(Authorization.INHIBIT);
							break;
						}
						continue;
					}

					_logger.debug("All specified execution actions executed successfully!");
					_authorizationAction.setAuthorization(curAuthAction.getAuthorization());
					break;
				} else {
					_logger.debug("Authorization action={} requires inhibiting event; adjusting decision",
							curAuthAction.getName());
					_authorizationAction.setAuthorization(Authorization.INHIBIT);
					break;
				}
			} while (true);
		}

		if (_authorizationAction.getAuthorization() == Authorization.INHIBIT) {
			_logger.debug("Decision requires inhibiting event; adjusting delay");
			_authorizationAction.setDelay(Math.max(_authorizationAction.getDelay(), curAuthAction.getDelay()));
		} else {
			_logger.debug("Decision allows event; copying modifiers (if present)");
			// TODO: modifier collision is not resolved here!
			for (Map.Entry<String,String> curParam : curAuthAction.getModifiers().entrySet())
				_authorizationAction.addModifier(curParam.getKey(), curParam.getValue());
		}

		_logger.debug("Processing asynchronous executeActions " + mech.getExecuteAsyncActions());
		for (ExecuteAction execAction : mech.getExecuteAsyncActions()) {
			if (execAction.getProcessor().equals("pep")) {
				_logger.debug("Copying executeAction {} for processing by pep", execAction.getName());
				_executeActions.add(execAction);
			} else {
				_logger.debug("Execute asynchronous action [{}]", execAction.getName());
				_pxpManager.execute(execAction, false);
			}
		}

	}

	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(getClass())
				.add("_authorizationAction", _authorizationAction)
				.add("_executeActions", _executeActions)
				.add("_pxpManager", _pxpManager)
				.toString();
	}

	public IResponse toResponse() {
		// Convert an (IESE) Decision object into a (TUM) Response
		IStatus status;

		try {
			if (_authorizationAction.getAuthorization() == Authorization.ALLOW) {
				if (_authorizationAction.getModifiers().size() == 0) {
					status = new StatusBasic(EStatus.ALLOW);
				}
				else {
					status = new StatusBasic(EStatus.MODIFY);
				}
			} else {
				status = new StatusBasic(EStatus.INHIBIT);
			}
		} catch (Exception e) {
			status = new StatusBasic(EStatus.ERROR, "PDP returned wrong status (" + e + ")");
		}

		List<IEvent> list = new ArrayList<>(_executeActions.size());

		for (ExecuteAction ea : _executeActions) {
			list.add(new EventBasic(ea.getName(), ea.getParameters(), false));
			// TODO: take care of processor. for the time being ignored by TUM
		}

		// FIXME: This looks odd. Each PEP would need to know about the fact that "triggerEvent" is returned
		// as an event name in case of MODIFY and the PEP would need to adjust the modified event
		// using the modifiers (getModifiers()) itself. Why not assemble the exact modified event and return it instead?! -FK-
		IEvent modifiedEvent = new EventBasic("triggerEvent", _authorizationAction.getModifiers());
		return new ResponseBasic(status, list, modifiedEvent);
	}
}
