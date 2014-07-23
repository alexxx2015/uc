package de.tum.in.i22.uc.pdp.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import javax.xml.bind.Unmarshaller;

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
	private static Logger _logger = LoggerFactory.getLogger(PolicyDecisionPoint.class);

	private static final String JAXB_CONTEXT = "de.tum.in.i22.uc.pdp.xsd";

	private final IPdp2Pip _pip;

	private final ActionDescriptionStore _actionDescriptionStore;

	private final HashMap<String, ArrayList<IPdpMechanism>> _policyTable = new HashMap<String, ArrayList<IPdpMechanism>>();
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

		InputStream inp = null;
		try {
			inp = new FileInputStream(policyFilename);
		} catch (FileNotFoundException e) {
			_logger.warn("Policy file " + policyFilename + " not found.");
			e.printStackTrace();
			return false;
		}

		return deployXML(inp);
	}

	private boolean deployXML(InputStream inp) {
		if (inp == null) {
			return false;
		}

		try {
			JAXBContext jc = JAXBContext.newInstance(JAXB_CONTEXT);
			Unmarshaller u = jc.createUnmarshaller();

			JAXBElement<?> poElement = (JAXBElement<?>) u.unmarshal(inp);
			PolicyType curPolicy = (PolicyType) poElement.getValue();

			_logger.debug("curPolicy [name={}]: {}", curPolicy.getName(), curPolicy.toString());

			List<MechanismBaseType> mechanisms = curPolicy.getDetectiveMechanismOrPreventiveMechanism();

			if (_policyTable.containsKey(curPolicy.getName())) {
				// log.error("Policy [{}] already deployed! Aborting...",
				// curPolicy.getName());
				// return false;
			}

			for (MechanismBaseType mech : mechanisms) {
				try {
					_logger.debug("Processing mechanism: {}", mech.getName());
					IPdpMechanism curMechanism = new Mechanism(mech, this);
					ArrayList<IPdpMechanism> mechanismList = _policyTable.get(curPolicy.getName());
					if (mechanismList == null)
						mechanismList = new ArrayList<IPdpMechanism>();
					if (mechanismList.contains(curMechanism)) {
						_logger.error("Mechanism [{}] is already deployed for policy [{}]",
								curMechanism.getMechanismName(), curPolicy.getName());
						continue;
					}

					mechanismList.add(curMechanism);
					_policyTable.put(curPolicy.getName(), mechanismList);

					_logger.debug("Starting mechanism update thread...");
					if (curMechanism instanceof Mechanism) {
						((Mechanism) curMechanism).init();
						((Mechanism) curMechanism).start();
					}
					_logger.info("Mechanism {} started...", curMechanism.getMechanismName());
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
		boolean ret = false;
		if (_policyTable == null) {
			_logger.error("Empty Policy Table. impossible to revoke policy");
			return false;
		}
		List<IPdpMechanism> mlist = _policyTable.get(policyName);
		if (mlist == null)
			return false;

		for (IPdpMechanism mech : mlist) {
			_logger.info("Revoking mechanism: {}", mech.getMechanismName());
			ret = mech.revoke();
		}
		return ret;
	}

	@Override
	public boolean revokeMechanism(String policyName, String mechName) {
		boolean ret = false;
		if (_policyTable == null) {
			_logger.error("Empty Policy Table. impossible to revoke policy");
			return false;
		}
		ArrayList<IPdpMechanism> mechanisms = _policyTable.get(policyName);
		if (mechanisms != null) {
			IPdpMechanism mech = null;
			for (IPdpMechanism m : mechanisms) {
				if (m.getMechanismName().equals(mechName)) {
					_logger.info("Revoking mechanism: {}", m.getMechanismName());
					ret = m.revoke();
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
			_logger.info("Processing mechanism [{}] for event [{}]", mech.getMechanismName(), event.getEventAction());
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
				mechanismList.add(m.getMechanismName());
			}
			map.put(policyName, mechanismList);
		}

		return map;
	}

	private String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		reader.close();
		return stringBuilder.toString();
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
		Set<String> _policyTableKeys = this._policyTable.keySet();
		Iterator<String> _policyTableKeysIt = _policyTableKeys.iterator();
		while (_policyTableKeysIt.hasNext()) {
			String _policyTableKey = _policyTableKeysIt.next();
			ArrayList<IPdpMechanism> mechanisms = _policyTable.get(_policyTableKey);
			Iterator<IPdpMechanism> mechanismsIt = mechanisms.iterator();
			while (mechanismsIt.hasNext()) {
				IPdpMechanism mechanism = mechanismsIt.next();
				if (mechanism instanceof Runnable) {
					((Thread) mechanism).interrupt();
				}
			}
		}
		this._policyTable.clear();
	}

}
