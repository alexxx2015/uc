package de.tum.in.i22.uc.pdp.core;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private final HashMap<String, List<IPdpMechanism>> _policyTable = new HashMap<String, List<IPdpMechanism>>();
	private final PxpManager _pxpManager;

	public PolicyDecisionPoint() {
		_actionDescriptionStore = new ActionDescriptionStore();
		_pxpManager = new PxpManager();
		_pip = new DummyPipProcessor();
	}

	public PolicyDecisionPoint(IPdp2Pip pip, PxpManager pxpManager) {
		_pxpManager = pxpManager;
		_actionDescriptionStore = new ActionDescriptionStore();
		_pip = pip;
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

			List<MechanismBaseType> mechanisms = policy.getDetectiveMechanismOrPreventiveMechanism();

			if (_policyTable.containsKey(policy.getName())) {
				// log.error("Policy [{}] already deployed! Aborting...",
				// curPolicy.getName());
				// return false;
			}

			for (MechanismBaseType mech : mechanisms) {
				try {
					_logger.debug("Processing mechanism: {}", mech.getName());
					IPdpMechanism curMechanism = new Mechanism(mech, this);

					List<IPdpMechanism> mechanismList = _policyTable.get(policy.getName());
					if (mechanismList == null)
						mechanismList = new ArrayList<IPdpMechanism>();
					if (mechanismList.contains(curMechanism)) {
						_logger.error("Mechanism [{}] is already deployed for policy [{}]",
								curMechanism.getName(), policy.getName());
						continue;
					}

					mechanismList.add(curMechanism);
					_policyTable.put(policy.getName(), mechanismList);

					_logger.debug("Starting mechanism update thread...");
					if (curMechanism instanceof Mechanism) {
						((Mechanism) curMechanism).init();
						new Thread(curMechanism).start();
					}
					_logger.info("Mechanism {} started...", curMechanism.getName());
				} catch (InvalidMechanismException e) {
					_logger.error("Invalid mechanism specified: {}", e.getMessage());
					return false;
				}
			}
			return true;
		} catch (UnmarshalException e) {
			_logger.error("Syntax error in policy: " + e.getMessage());
		} catch (JAXBException | ClassCastException e) {
			_logger.error("Error while deploying policy: " + e.getMessage());
		}
		return false;
	}

	@Override
	public boolean revokePolicy(String policyName) {
		List<IPdpMechanism> mlist = _policyTable.get(policyName);
		if (mlist == null)
			return false;

		for (IPdpMechanism mech : mlist) {
			_logger.info("Revoking mechanism: {}", mech.getName());
			mech.revoke();
		}

		return true;
	}

	@Override
	public boolean revokeMechanism(String policyName, String mechName) {
		boolean ret = false;

		List<IPdpMechanism> mechanisms = _policyTable.get(policyName);
		if (mechanisms != null) {
			IPdpMechanism mech = null;
			for (IPdpMechanism m : mechanisms) {
				if (m.getName().equals(mechName)) {
					_logger.info("Revoking mechanism: {}", m.getName());
					m.revoke();
					ret = true;
					mech = m;
					break;
				}
			}
			if (mech != null) {
				mechanisms.remove(mech);
				if (mech instanceof Mechanism) {
					_actionDescriptionStore.removeMechanism(((Mechanism) mech).getTriggerEvent().getAction());
				}
			}
		}
		return ret;
	}

	@Override
	public Decision notifyEvent(Event event) {
		ArrayList<EventMatch> eventMatchList = _actionDescriptionStore.getEventList(event.getEventAction());
		if (eventMatchList == null)
			eventMatchList = new ArrayList<EventMatch>();
		_logger.debug("Searching for subscribed condition nodes for event=[{}] -> subscriptions: {}",
				event.getEventAction(), eventMatchList.size());
		for (EventMatch eventMatch : eventMatchList) {
			_logger.info("Processing EventMatchOperator for event [{}]", eventMatch.getAction());
			eventMatch.evaluate(event);
		}

		ArrayList<Mechanism> mechanismList = _actionDescriptionStore.getMechanismList(event.getEventAction());
		if (mechanismList == null)
			mechanismList = new ArrayList<Mechanism>();
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
	public Map<String, List<String>> listDeployedMechanisms() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		for (String policyName : _policyTable.keySet()) {
			List<String> mechanismList = new ArrayList<String>();
			for (IPdpMechanism m : _policyTable.get(policyName)) {
				mechanismList.add(m.getName());
			}
			map.put(policyName, mechanismList);
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
			List<IPdpMechanism> mechanisms = _policyTable.get(_policyTableKey);
			Iterator<IPdpMechanism> mechanismsIt = mechanisms.iterator();
			while (mechanismsIt.hasNext()) {
				mechanismsIt.next().revoke();
			}
		}
		_policyTable.clear();
	}

}
