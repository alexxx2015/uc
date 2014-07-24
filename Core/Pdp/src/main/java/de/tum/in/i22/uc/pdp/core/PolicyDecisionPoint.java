package de.tum.in.i22.uc.pdp.core;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.pdp.PxpManager;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.core.shared.Constants;
import de.tum.in.i22.uc.pdp.core.shared.Decision;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.core.shared.IPdpMechanism;
import de.tum.in.i22.uc.pdp.core.shared.IPolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PolicyType;

public class PolicyDecisionPoint implements IPolicyDecisionPoint {
	private static final Logger _logger = LoggerFactory.getLogger(PolicyDecisionPoint.class);

	private static final String JAXB_CONTEXT = "de.tum.in.i22.uc.pdp.xsd";

	private final IPdp2Pip _pip;

	private final ActionDescriptionStore _actionDescriptionStore;

	/**
	 * Maps policy names to its set of mechanisms, where for each
	 * mechanism the mechanism name maps to the actual mechanism
	 */
	private final HashMap<String, Map<String,IPdpMechanism>> _policyTable;

	private final PxpManager _pxpManager;

	public PolicyDecisionPoint() {
		this(new DummyPipProcessor(), new PxpManager());
	}

	public PolicyDecisionPoint(IPdp2Pip pip, PxpManager pxpManager) {
		_pip = pip;
		_pxpManager = pxpManager;
		_policyTable = new HashMap<String, Map<String,IPdpMechanism>>();
		_actionDescriptionStore = new ActionDescriptionStore();
	}

	@Override
	public boolean deployPolicyXML(XmlPolicy xmlPolicy) {
		_logger.debug("deployPolicyXML: " + xmlPolicy.getName());
		return deployXML(new ByteArrayInputStream(xmlPolicy.getXml().getBytes()));
	}

	@Override
	public boolean deployPolicyURI(String policyFilename) {
		_logger.warn("Unsupported message format of policy! " + policyFilename + ". Not deploying policy.");
		if (!policyFilename.endsWith(".xml")) {
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

			_logger.debug("deploying policy [name={}]: {}", policy.getName(), policy.toString());

			/*
			 * Get the set of mechanisms of this policy (if any)
			 */
			Map<String,IPdpMechanism> mechanisms = _policyTable.get(policy.getName());
			if (mechanisms == null) {
				mechanisms = new HashMap<String,IPdpMechanism>();
				_policyTable.put(policy.getName(), mechanisms);
			}

			/*
			 * Loop over all mechanisms, add them to the set of mechanisms
			 * for this policy, and start the mechanism
			 */
			for (MechanismBaseType mech : policy.getDetectiveMechanismOrPreventiveMechanism()) {
				try {
					_logger.debug("Processing mechanism: {}", mech.getName());
					IPdpMechanism curMechanism = new Mechanism(mech, policy.getName(), this);

					if (!mechanisms.containsKey(mech.getName())) {
						_logger.debug("Starting mechanism update thread...: " + curMechanism.getName());
						mechanisms.put(mech.getName(), curMechanism);
						new Thread(curMechanism).start();
					}
					else {
						_logger.warn("Mechanism [{}] is already deployed for policy [{}]", curMechanism.getName(), policy.getName());
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

	@Override
	public void revokePolicy(String policyName) {
		_logger.debug("revokePolicy({}) invoked.", policyName);

		Map<String,IPdpMechanism> mechanisms = _policyTable.remove(policyName);
		if (mechanisms == null) {
			_logger.warn("Policy {} was not deployed. Unable to revoke.", policyName);
			return;
		}

		for (IPdpMechanism mech : mechanisms.values()) {
			_logger.info("Revoking mechanism: {}", mech.getName());
			mech.revoke();
		}
	}

	@Override
	public boolean revokeMechanism(String policyName, String mechName) {
		_logger.info("revokeMechanism({}, {}) invoked.", policyName, mechName);

		Map<String,IPdpMechanism> mechanisms = _policyTable.get(policyName);
		if (mechanisms == null) {
			return false;
		}

		IPdpMechanism mech = mechanisms.remove(mechName);
		if (mech == null) {
			return false;
		}

		_logger.info("Revoking mechanism: {}", mechName);
		mech.revoke();
		_actionDescriptionStore.removeMechanism(mech.getTriggerEvent().getAction());

		return true;
	}

	@Override
	public Decision notifyEvent(Event event) {
		List<EventMatch> eventMatchList = _actionDescriptionStore.getEventList(event.getEventAction());
		if (eventMatchList == null)
			eventMatchList = new LinkedList<EventMatch>();
		_logger.debug("Searching for subscribed condition nodes for event=[{}] -> subscriptions: {}",
				event.getEventAction(), eventMatchList.size());
		for (EventMatch eventMatch : eventMatchList) {
			_logger.info("Processing EventMatchOperator for event [{}]", eventMatch.getAction());
			eventMatch.evaluate(event);
		}

		List<Mechanism> mechanismList = _actionDescriptionStore.getMechanismList(event.getEventAction());
		if (mechanismList == null)
			mechanismList = new LinkedList<Mechanism>();
		_logger.debug("Searching for triggered mechanisms for event=[{}] -> subscriptions: {}", event.getEventAction(),
				mechanismList.size());

		Decision d = new Decision(new AuthorizationAction("default", Constants.AUTHORIZATION_ALLOW), _pxpManager);
		for (Mechanism mech : mechanismList) {
			_logger.info("Processing mechanism [{}] for event [{}]", mech.getName(), event.getEventAction());
			mech.notifyEvent(event, d);
		}
		return d;
	}

	@Override
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

	@Override
	public IPdp2Pip getPip() {
		return _pip;
	}

	@Override
	public ActionDescriptionStore getActionDescriptionStore() {
		return _actionDescriptionStore;
	}

	@Override
	public PxpManager getPxpManager() {
		return _pxpManager;
	}

	@Override
	public void stop() {
		Set<String> _policyTableKeys = _policyTable.keySet();
		Iterator<String> _policyTableKeysIt = _policyTableKeys.iterator();
		while (_policyTableKeysIt.hasNext()) {
			String _policyTableKey = _policyTableKeysIt.next();
			Map<String,IPdpMechanism> mechanisms = _policyTable.get(_policyTableKey);
			Iterator<IPdpMechanism> mechanismsIt = mechanisms.values().iterator();
			while (mechanismsIt.hasNext()) {
				mechanismsIt.next().revoke();
			}
		}
		_policyTable.clear();
	}

}
