package de.tum.in.i22.uc.pdp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.PxpManager;
import de.tum.in.i22.uc.pdp.core.condition.Condition;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.core.shared.Decision;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.core.shared.IPdpAuthorizationAction;
import de.tum.in.i22.uc.pdp.core.shared.IPdpExecuteAction;
import de.tum.in.i22.uc.pdp.core.shared.IPdpMechanism;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationActionType;
import de.tum.in.i22.uc.pdp.xsd.ExecuteAsyncActionType;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PreventiveMechanismType;

public class Mechanism implements IPdpMechanism {
	private static Logger _logger = LoggerFactory.getLogger(Mechanism.class);

	private String _name = null;
	private String _description = null;
	private long _lastUpdate = 0;
	private long _timestepSize = 0;
	private long _timestep = 0;
	private EventMatch _triggerEvent = null;
	private Condition _condition = null;
	private IPdpAuthorizationAction _authorizationAction = null;
	private List<IPdpExecuteAction> _executeAsyncActions = new ArrayList<IPdpExecuteAction>();
	private PolicyDecisionPoint _pdp = null;
	private boolean _interrupted = false;
	private PxpManager _pxpManager;

	/**
	 * The name of the policy of which this mechanism is a part of
	 */
	private final String _policyName;

	public Mechanism(MechanismBaseType mech, String policyName, PolicyDecisionPoint pdp) throws InvalidMechanismException {
		_logger.debug("Preparing mechanism from MechanismBaseType");
		_pdp = pdp;
		if (pdp == null) {
			_logger.error("Impossible to take proper decision with a null pdp. failing miserably");
			throw new RuntimeException();
		}

		_policyName = policyName;
		_pxpManager = pdp.getPxpManager();
		_name = mech.getName();
		_description = mech.getDescription();
		_lastUpdate = 0;
		_timestepSize = mech.getTimestep().getAmount() * TimeAmount.getTimeUnitMultiplier(mech.getTimestep().getUnit());
		_timestep = 0;

		if (mech instanceof PreventiveMechanismType) {
			PreventiveMechanismType curMech = (PreventiveMechanismType) mech;
			_logger.debug("Processing PreventiveMechanism");

			_triggerEvent = new EventMatch(curMech.getTrigger(), this);

			ActionDescriptionStore ads = pdp.getActionDescriptionStore();
			ads.addMechanism(this);

			// TODO: subscription to PEP?!

			_logger.debug("Preparing AuthorizationAction from List<AuthorizationActionType>: {} entries", curMech
					.getAuthorizationAction().size());
			HashMap<String, AuthorizationAction> authActions = new HashMap<String, AuthorizationAction>();

			for (AuthorizationActionType auth : curMech.getAuthorizationAction()) {
				_logger.debug("Found authAction {}", auth.getName());
				if (auth.isSetStart() || curMech.getAuthorizationAction().size() == 1)
					authActions.put("start", new AuthorizationAction(auth));
				else
					authActions.put(auth.getName(), new AuthorizationAction(auth));
			}

			_logger.debug("Preparing hierarchy of authorizationActions (list: {})", authActions.size());
			_authorizationAction = authActions.get("start");

			if (curMech.getAuthorizationAction().size() > 1) {
				IPdpAuthorizationAction curAuth = _authorizationAction;
				_logger.debug("starting with curAuth: {}", curAuth.getName());
				do {
					_logger.debug("searching for fallback={}", curAuth.getFallbackName());
					if (!curAuth.getFallbackName().equalsIgnoreCase("allow")
							&& !curAuth.getFallbackName().equalsIgnoreCase("inhibit")) {
						IPdpAuthorizationAction fallbackAuth = authActions.get(curAuth.getFallbackName());
						if (fallbackAuth == null) {
							_logger.error("Requested fallback authorizationAction {} not found!", curAuth.getFallbackName());
							throw new InvalidMechanismException("Requested fallback authorizationAction not specified");
						}
						curAuth.setFallback(fallbackAuth);
						_logger.debug("  set fallback to {}", curAuth.getFallback().getName());
					} else {
						if (curAuth.getFallbackName().equalsIgnoreCase("allow")) {
							curAuth.setFallback(AuthorizationAction.AUTHORIZATION_ALLOW);
						}
						_logger.debug("  set fallback to static {}", curAuth.getFallback().getName());
						break;
					}
					curAuth = curAuth.getFallback();
				} while (true);
			}
			_logger.debug("AuthorizationActions successfully processed.");
		}

		_condition = new Condition(mech.getCondition(), this);

		_logger.debug("Processing executeAsyncActions");
		// Processing synchronous executeActions for allow
		for (ExecuteAsyncActionType execAction : mech.getExecuteAsyncAction()) {
			_executeAsyncActions.add(new ExecuteAction(execAction));
		}
	}

	@Override
	public String getName() {
		return _name;
	}
	@Override
	public IPdpAuthorizationAction getAuthorizationAction() {
		return _authorizationAction;
	}

	@Override
	public List<IPdpExecuteAction> getExecuteAsyncActions() {
		return _executeAsyncActions;
	}

	@Override
	public EventMatch getTriggerEvent() {
		return _triggerEvent;
	}

	@Override
	public long getTimestepSize() {
		return _timestepSize;
	}

	@Override
	public void revoke() {
		_interrupted = true;
	}

	public synchronized Decision notifyEvent(Event curEvent, Decision d) {
		_logger.debug("updating mechanism [{}]", _name);
		if (_triggerEvent.eventMatches(curEvent)) {
			_logger.info("Event matches -> evaluating condition");
			boolean ret = _condition.evaluate(curEvent);
			if (ret) {
				_logger.info("Condition satisfied; merging mechanism into decision");
				d.processMechanism(this, curEvent);
			} else
				_logger.info("condition NOT satisfied");
		}

		return d;
	}

	private synchronized boolean mechanismUpdate() { // TODO improve accuracy to
		// microseconds?
		long now = System.currentTimeMillis();
		long elapsedLastUpdate = now - _lastUpdate;
		long difference = elapsedLastUpdate - _timestepSize / 1000;

		if (difference < 0) { // Aborting update because the timestep has not
			// yet passed
			_logger.trace("[{}] Timestep remaining {} -> timestep has not yet passed", _name, difference);
			_logger.trace("##############################################################################################################");
			return false;
		}

		// Correct time substracting possible delay in the execution because
		// difference between timestep and last time
		// mechanism was updated will not be exactly the timestepSize
		_lastUpdate = now - difference;
		if (difference > _timestepSize) {
			_logger.warn(
					"[{}] Timestep difference is larger than mechanism's timestep size => we missed to evaluate at least one timestep!!",
					_name);
			_logger.warn("--------------------------------------------------------------------------------------------------------------");
		}

		_timestep++;
		_logger.debug("////////////////////////////////////////////////////////////////////////////////////////////////////////////");
		_logger.debug("[{}] Null-Event updating {}. timestep at interval of {} us", _name, _timestep,
				_timestepSize);

		boolean conditionValue = _condition.evaluate(null);
		_logger.debug("conditionValue: {}", conditionValue);
		_logger.debug("////////////////////////////////////////////////////////////////////////////////////////////////////////////");

		return conditionValue;
	}

	@Override
	public void run() {
		long sleepValue = _timestepSize / 1000;
		_logger.info("Started mechanism update thread usleep={} ms", sleepValue);

		_lastUpdate = System.currentTimeMillis();
		_condition.operator.initOperatorForMechanism(this);

		while (!_interrupted) {
			try {
				boolean mechanismValue = mechanismUpdate();
				if (mechanismValue) {
					_logger.info("Mechanism condition satisfied; triggered optional executeActions");
					for (IPdpExecuteAction execAction : getExecuteAsyncActions()) {
						if (execAction.getProcessor().equals("pep"))
							_logger.warn("Timetriggered execution of executeAction [{}] not possible with processor PEP",
									execAction.getName());
						else {
							_logger.debug("Execute asynchronous action [{}]", execAction.getName());
							_pxpManager.execute(execAction, false);
						}
					}
				}

				if (_interrupted) {
					_logger.info("Mechanism [{}] thread was interrupted. terminating...", _name);
					return;
				}

				Thread.sleep(sleepValue);
			} catch (InterruptedException e) {
				_logger.info("[InterruptedException] Mechanism [{}] was interrupted. terminating...", _name);
			}
		}
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this.getClass())
				.add("_name", _name)
				.add("_description", _description)
				.add("_timestepSize", _timestepSize)
				.add("_lastUpdate", _lastUpdate)
				.add("_timestep", _timestep)
				.add("_triggerEvent", _triggerEvent)
				.add("_condition", _condition)
				.add("_authorizationAction", _authorizationAction)
				.add("_executeActions", _executeAsyncActions)
				.toString();
	}

	@Override
	public List<IPdpExecuteAction> getExecuteActions() {
		return _executeAsyncActions;
	}

	@Override
	public PolicyDecisionPoint getPolicyDecisionPoint() {
		return _pdp;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Mechanism) {
			Mechanism other = (Mechanism) obj;
			return Objects.equals(_name, other._name)
					&& Objects.equals(_policyName, other._policyName);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_name, _policyName);
	}
}
