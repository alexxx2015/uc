package de.tum.in.i22.uc.pdp.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.pdp.PxpManager;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.core.shared.Constants;
import de.tum.in.i22.uc.pdp.core.shared.Decision;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.core.shared.IPdpMechanism;
import de.tum.in.i22.uc.pdp.core.shared.IPolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PolicyType;

public class PolicyDecisionPoint implements IPolicyDecisionPoint, Serializable {
	private static Logger log = LoggerFactory
			.getLogger(PolicyDecisionPoint.class);
	private static final long serialVersionUID = -6823961095919408237L;

	private static IPdp2Pip _pip;

	private static IPolicyDecisionPoint instance = null;
	private ActionDescriptionStore _actionDescriptionStore = null;
	private final HashMap<String, ArrayList<IPdpMechanism>> policyTable = new HashMap<String, ArrayList<IPdpMechanism>>();
	private PxpManager _pxpManager;

	public PolicyDecisionPoint() {
		_actionDescriptionStore = new ActionDescriptionStore();
		_pxpManager=new PxpManager();
	}

	public PolicyDecisionPoint(IPdp2Pip pip, PxpManager pxpManager) {
		_pxpManager=pxpManager;
		_actionDescriptionStore = new ActionDescriptionStore();
		_pip=pip;
	}
	@Override
	public boolean deployPolicyXML(String XMLPolicy) {
		log.debug("deployPolicyXML (before)");
		InputStream is = new ByteArrayInputStream(XMLPolicy.getBytes());
		log.debug("deployPolicyXML (IS created)");
		boolean b = deployXML(is);
		log.debug("deployPolicyXML (after)");
		return b;
	}

	@Override
	public boolean deployPolicyURI(String policyFilename) {
		if (policyFilename.endsWith(".xml")) {
			InputStream inp = null;
			try {
				inp = new FileInputStream(policyFilename);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return deployXML(inp);
		}
		log.warn("Unsupported message format of policy! "+policyFilename );
		return false;
	}

	public boolean deployXML(InputStream inp) {
		if (inp == null)
			return false;
		try {
			JAXBContext jc = JAXBContext
					.newInstance("de.tum.in.i22.uc.pdp.xsd");
			Unmarshaller u = jc.createUnmarshaller();

			JAXBElement<?> poElement = (JAXBElement<?>) u.unmarshal(inp);
			PolicyType curPolicy = (PolicyType) poElement.getValue();

			log.debug("curPolicy [name={}]: {}", curPolicy.getName(),
					curPolicy.toString());

			List<MechanismBaseType> mechanisms = curPolicy
					.getDetectiveMechanismOrPreventiveMechanism();

			if (this.policyTable.containsKey(curPolicy.getName())) {
				// log.error("Policy [{}] already deployed! Aborting...",
				// curPolicy.getName());
				// return false;
			}

			for (MechanismBaseType mech : mechanisms) {
				try {
					log.debug("Processing mechanism: {}", mech.getName());
					IPdpMechanism curMechanism = new Mechanism(mech,this);
					ArrayList<IPdpMechanism> mechanismList = this.policyTable
							.get(curPolicy.getName());
					if (mechanismList == null)
						mechanismList = new ArrayList<IPdpMechanism>();
					if (mechanismList.contains(curMechanism)) {
						log.error(
								"Mechanism [{}] is already deployed for policy [{}]",
								curMechanism.getMechanismName(),
								curPolicy.getName());
						continue;
					}

					mechanismList.add(curMechanism);
					this.policyTable.put(curPolicy.getName(), mechanismList);

					log.debug("Starting mechanism update thread...");
					if (curMechanism instanceof Mechanism) {
						((Mechanism) curMechanism).init();
					}
					log.info("Mechanism {} started...",
							curMechanism.getMechanismName());
				} catch (InvalidMechanismException e) {
					log.error("Invalid mechanism specified: {}", e.getMessage());
					return false;
				}
			}
			return true;
		} catch (UnmarshalException e) {
			log.error("Syntax error in policy: " + e.getMessage());
		} catch (JAXBException | ClassCastException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean revokePolicy(String policyName) {
		boolean ret = false;
		if (this.policyTable == null) {
			log.error("Empty Policy Table. impossible to revoke policy");
			return false;
		}
		List<IPdpMechanism> mlist = this.policyTable.get(policyName);
		if (mlist == null)
			return false;

		for (IPdpMechanism mech : mlist) {
			log.info("Revoking mechanism: {}", mech.getMechanismName());
			ret = mech.revoke();
		}
		return ret;
	}

	@Override
	public boolean revokeMechanism(String policyName, String mechName) {
		boolean ret = false;
		if (this.policyTable == null) {
			log.error("Empty Policy Table. impossible to revoke policy");
			return false;
		}
		ArrayList<IPdpMechanism> mechanisms = this.policyTable.get(policyName);
		if (mechanisms != null) {
			IPdpMechanism mech = null;
			for (IPdpMechanism m : mechanisms) {
				if (m.getMechanismName().equals(mechName)) {
					log.info("Revoking mechanism: {}", m.getMechanismName());
					ret = m.revoke();
					mech = m;
					break;
				}
			}
			if (mech != null) {
				mechanisms.remove(mech);
				if (mech instanceof Mechanism) {
					this._actionDescriptionStore
							.removeMechanism(((Mechanism) mech)
									.getTriggerEvent().getAction());
				}
			}
		}
		return ret;
	}

	@Override
	public Decision notifyEvent(Event event) {
		ArrayList<EventMatch> eventMatchList = this._actionDescriptionStore
				.getEventList(event.getEventAction());
		if (eventMatchList == null)
			eventMatchList = new ArrayList<EventMatch>();
		log.debug(
				"Searching for subscribed condition nodes for event=[{}] -> subscriptions: {}",
				event.getEventAction(), eventMatchList.size());
		for (EventMatch eventMatch : eventMatchList) {
			log.info("Processing EventMatchOperator for event [{}]",
					eventMatch.getAction());
			eventMatch.evaluate(event);
		}

		ArrayList<Mechanism> mechanismList = this._actionDescriptionStore
				.getMechanismList(event.getEventAction());
		if (mechanismList == null)
			mechanismList = new ArrayList<Mechanism>();
		log.debug(
				"Searching for triggered mechanisms for event=[{}] -> subscriptions: {}",
				event.getEventAction(), mechanismList.size());

		Decision d = new Decision(new AuthorizationAction("default",
				Constants.AUTHORIZATION_ALLOW),_pxpManager);
		for (Mechanism mech : mechanismList) {
			log.info("Processing mechanism [{}] for event [{}]",
					mech.getMechanismName(), event.getEventAction());
			mech.notifyEvent(event, d);
		}
		return d;
	}

	@Override
	public Map<String, List<String>> listDeployedMechanisms() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		for (String policyName : this.policyTable.keySet()) {
			List<String> mechanismList = new ArrayList<String>();
			for (IPdpMechanism m : this.policyTable.get(policyName)) {
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
		return this._actionDescriptionStore;
	}

	@Override
	public PxpManager getPxpManager(){
		return _pxpManager;
	}
	
}
