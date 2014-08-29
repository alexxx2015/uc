package de.tum.in.i22.uc.pdp.core;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.pdp.PxpManager;
import de.tum.in.i22.uc.pdp.core.AuthorizationAction.Authorization;
import de.tum.in.i22.uc.pdp.core.condition.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.condition.operators.StateBasedOperator;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.core.mechanisms.MechanismFactory;
import de.tum.in.i22.uc.pdp.distribution.DistributedPdpResponse;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PolicyType;

public class PolicyDecisionPoint extends Observable implements Observer {
	private static final Logger _logger = LoggerFactory.getLogger(PolicyDecisionPoint.class);

	private static final String JAXB_CONTEXT = "de.tum.in.i22.uc.pdp.xsd";

	private final IPdp2Pip _pip;

	private final ActionDescriptionStore _actionDescriptionStore;

	/**
	 * Maps policy names to its set of mechanisms, where for each mechanism the
	 * mechanism name maps to the actual mechanism
	 */
	private final Map<String, Map<String, Mechanism>> _policyTable;

	private final PxpManager _pxpManager;

	/**
	 * Accumulates all changed {@link OperatorState}s during
	 * an ongoing event evaluation ({@link PolicyDecisionPoint#notifyEvent(IEvent)}).
	 */

	private final List<EventMatchOperator> _eventMatches;

	private final List<StateBasedOperator> _stateBasedOperatorTrue;

	public PolicyDecisionPoint() {
		this(new DummyPipProcessor(), new PxpManager());
	}

	public PolicyDecisionPoint(IPdp2Pip pip, PxpManager pxpManager) {
		_pip = pip;
		_pxpManager = pxpManager;
		_stateBasedOperatorTrue = new LinkedList<>();
		_eventMatches = new LinkedList<>();
		_policyTable = new HashMap<String, Map<String, Mechanism>>();
		_actionDescriptionStore = new ActionDescriptionStore();
	}

	public boolean deployPolicyXML(XmlPolicy xmlPolicy) {
		_logger.debug("deployPolicyXML: " + xmlPolicy.getName());
		return deployXML(new ByteArrayInputStream(xmlPolicy.getXml().getBytes()));
	}

	public boolean deployPolicyURI(String policyFilename) {
		if (!policyFilename.endsWith(".xml")) {
			_logger.warn("Unsupported message format of policy [" + policyFilename + "]. Not deploying policy.");
			return false;
		}

		InputStream is = null;
		try {
			is = new FileInputStream(policyFilename);
		} catch (FileNotFoundException e) {
			_logger.warn("Policy file " + policyFilename + " not found.");
			e.printStackTrace();
			return false;
		}

		return deployXML(is);
	}

	private boolean deployXML(InputStream is) {
		if (is == null) {
			return false;
		}

		try {
			JAXBElement<?> poElement = (JAXBElement<?>) JAXBContext.newInstance(JAXB_CONTEXT).createUnmarshaller().unmarshal(is);
			PolicyType policy = (PolicyType) poElement.getValue();
			final String policyName = policy.getName();

			_logger.debug("Deploying policy [name={}]", policyName);

			setChanged();
			notifyObservers(policy);

			/*
			 * Get the set of mechanisms of this policy (if any)
			 */
			Map<String, Mechanism> allMechanisms = _policyTable.get(policyName);
			if (allMechanisms == null) {
				allMechanisms = new HashMap<String, Mechanism>();
				_policyTable.put(policyName, allMechanisms);
			}

			/*
			 * Loop over all mechanisms, add them to the set of mechanisms for
			 * this policy, and start the mechanism
			 */
			for (MechanismBaseType mech : policy.getDetectiveMechanismOrPreventiveMechanism()) {
				try {
					_logger.debug("Processing mechanism: {}", mech.getName());
					Mechanism curMechanism = MechanismFactory.create(mech, policyName, this);

					if (!allMechanisms.containsKey(mech.getName())) {
						_logger.debug("Starting mechanism update thread...: " + curMechanism.getName());
						allMechanisms.put(mech.getName(), curMechanism);
						new Thread(curMechanism).start();
					} else {
						_logger.warn("Mechanism [{}] is already deployed for policy [{}]", curMechanism.getName(),
								policyName);
					}
				} catch (InvalidMechanismException e) {
					_logger.error("Invalid mechanism specified: {}", e.getMessage());
					return false;
				}
			}
		} catch (UnmarshalException e) {
			_logger.error("Syntax error in policy: " + e.getMessage());
			return false;
		} catch (JAXBException | ClassCastException e) {
			_logger.error("Error while deploying policy: " + e.getMessage());
			return false;
		}

		return true;
	}

	public void revokePolicy(String policyName) {
		_logger.debug("revokePolicy({}) invoked.", policyName);

		Map<String, Mechanism> mechanisms = _policyTable.remove(policyName);
		if (mechanisms == null) {
			_logger.warn("Policy {} was not deployed. Unable to revoke.", policyName);
			return;
		}

		for (Mechanism mech : mechanisms.values()) {
			_logger.info("Revoking mechanism: {}", mech.getName());
			mech.revoke();
		}
	}

	public boolean revokeMechanism(String policyName, String mechName) {
		_logger.info("revokeMechanism({}, {}) invoked.", policyName, mechName);

		Mechanism mech;
		Map<String, Mechanism> mechanisms = _policyTable.get(policyName);

		if (mechanisms == null || (mech = mechanisms.remove(mechName)) == null) {
			_logger.info("Mechanism [{}] did not exist for policy [{}] and could not be revoked.", mechName, policyName);
			return false;
		}

		_logger.info("Revoking mechanism: {}", mechName);
		mech.revoke();
		_actionDescriptionStore.removeMechanism(mech.getTriggerEvent().getAction());

		return true;
	}

	public IResponse notifyEvent(IEvent event) {
		Decision d = new Decision(new AuthorizationAction("default", Authorization.ALLOW), _pxpManager);

		List<EventMatch> eventMatchList = _actionDescriptionStore.getEventList(event.getName());
		if (eventMatchList != null) {
			_logger.debug("Searching for subscribed condition nodes for event=[{}] -> subscriptions: {}",
					event.getName(), eventMatchList.size());

			for (EventMatch eventMatch : eventMatchList) {
				_logger.info("Processing EventMatchOperator for event [{}]", eventMatch.getAction());
				eventMatch.evaluate(event);
			}
		}

		List<Mechanism> mechanismList = _actionDescriptionStore.getMechanismList(event.getName());
		_logger.debug("Searching for triggered mechanisms for event=[{}] -> subscriptions: {}", event.getName(),
				mechanismList.size());

		for (Mechanism mech : mechanismList) {
			_logger.info("Processing mechanism [{}] for event [{}]", mech.getName(), event.getName());
			mech.notifyEvent(event, d);
		}

		if (_stateBasedOperatorTrue.isEmpty() && _eventMatches.isEmpty()) {
			return d.toResponse();
		}

		IResponse response = new DistributedPdpResponse(d.toResponse(), _eventMatches, _stateBasedOperatorTrue);

		// prepare for next
		_eventMatches.clear();
		_stateBasedOperatorTrue.clear();

		return response;
	}

	public Map<String, Set<String>> listDeployedMechanisms() {
		Map<String, Set<String>> map = new TreeMap<String, Set<String>>();

		for (String policyName : _policyTable.keySet()) {
			Set<String> mechanisms = new TreeSet<String>();
			for (String mechName : _policyTable.get(policyName).keySet()) {
				mechanisms.add(mechName);
			}
			map.put(policyName, mechanisms);
		}

		return map;
	}

	public IPdp2Pip getPip() {
		return _pip;
	}

	public boolean executeAction(ExecuteAction execAction, boolean synchronous) {
		return _pxpManager.execute(execAction, synchronous);
	}

	public void stop() {
		for (Map<String, Mechanism> map : _policyTable.values()) {
			for (Mechanism mech : map.values()) {
				mech.revoke();
			}
		}

		_policyTable.clear();
	}

	public void addEventMatch(EventMatch eventMatch) {
		_actionDescriptionStore.addEventMatch(eventMatch);
	}

	public void addMechanism(Mechanism mechanism) {
		_actionDescriptionStore.addMechanism(mechanism);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof EventMatchOperator) {
			_eventMatches.add((EventMatchOperator) o);
		}
		else if (o instanceof StateBasedOperator) {
			_stateBasedOperatorTrue.add((StateBasedOperator) o);
		}
		else if ((o instanceof Mechanism) && (arg == Mechanism.END_OF_TIMESTEP)) {
			if (!_stateBasedOperatorTrue.isEmpty()) {
				setChanged();
				notifyObservers(new DistributedPdpResponse(new ResponseBasic(), Collections.<EventMatchOperator> emptyList(), _stateBasedOperatorTrue));
			}

			// Prepare for next
			_stateBasedOperatorTrue.clear();
		}
	}
}