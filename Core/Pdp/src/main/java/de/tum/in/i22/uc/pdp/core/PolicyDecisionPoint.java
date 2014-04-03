package de.tum.in.i22.uc.pdp.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

	private static IPolicyDecisionPoint instance = null;
	private ActionDescriptionStore actionDescriptionStore = null;
	private final HashMap<String, ArrayList<IPdpMechanism>> policyTable = new HashMap<String, ArrayList<IPdpMechanism>>();

	private PolicyDecisionPoint() {
		this.actionDescriptionStore = ActionDescriptionStore.getInstance();
	}

	public static IPolicyDecisionPoint getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (instance == null) {
			synchronized (PolicyDecisionPoint.class) {
				if (instance == null){
					instance = new PolicyDecisionPoint();
//					instance.deployPolicy("C:\\GIT\\pdp\\Core\\Pdp\\src\\main\\resources\\testTUM.xml");
				}
			}
		}
		return instance;
	}

	
	@Override
	public boolean deployPolicyXML(String XMLPolicy) {
		log.error("deployXML not yet supported");
		return false;
	}

	
	@Override
	public boolean deployPolicyURI(String policyFilename) {
		if (policyFilename.endsWith(".xml"))
			return deployXML(policyFilename);
		log.warn("Unsupported message format of policy!");
		return false;
	}

	public boolean deployXML(String xmlFilename) {
		try {
			JAXBContext jc = JAXBContext
					.newInstance("de.tum.in.i22.uc.pdp.xsd");
			Unmarshaller u = jc.createUnmarshaller();

			// SchemaFactory sf = SchemaFactory.newInstance(
			// XMLConstants.W3C_XML_SCHEMA_NS_URI );
			// Schema schema = sf.newSchema( new
			// File("src/main/resources/xsd/enfLanguage.xsd"));
			// //Schema schema = sf.newSchema(
			// PolicyDecisionPoint.class.getResource("xsd/enfLanguage.xsd").toURI().toURL());
			// u.setSchema(schema);
			// u.setEventHandler(new PolicyValidationEventHandler());

			log.info("Deploying policy from file [{}]", xmlFilename);
			JAXBElement<?> poElement = (JAXBElement<?>) u
					.unmarshal(new FileInputStream(xmlFilename));
			PolicyType curPolicy = (PolicyType) poElement.getValue();

			log.debug("curPolicy [name={}]: {}", curPolicy.getName(),
					curPolicy.toString());

			List<MechanismBaseType> mechanisms = curPolicy
					.getDetectiveMechanismOrPreventiveMechanism();

			if (this.policyTable.containsKey(curPolicy.getName())) {
//				log.error("Policy [{}] already deployed! Aborting...", curPolicy.getName());
//				return false;
			}

			for (MechanismBaseType mech : mechanisms) {
				try {
					log.debug("Processing mechanism: {}", mech.getName());

					IPdpMechanism curMechanism = new Mechanism(mech);
					ArrayList<IPdpMechanism> mechanismList = this.policyTable.get(curPolicy.getName());
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
						((Mechanism) curMechanism).init(this);
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
		} catch (JAXBException | FileNotFoundException | ClassCastException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean revokePolicy(String policyName) {
		boolean ret = false;
		if (this.policyTable==null){
			log.error("Empty Policy Table. impossible to revoke policy");
			return false;
		}
		List<IPdpMechanism> mlist = this.policyTable.get(policyName);
		if (mlist==null) return false;
		
		for (IPdpMechanism mech : mlist) {
			log.info("Revoking mechanism: {}", mech.getMechanismName());
			ret = mech.revoke();
		}
		return ret;
	}

	@Override
	public boolean revokeMechanism(String policyName, String mechName) {
		boolean ret = false;
		if (this.policyTable==null){
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
					this.actionDescriptionStore.removeMechanism(((Mechanism) mech).getTriggerEvent().getAction());
				}
			}
		}
		return ret;
	}

	@Override
	public Decision notifyEvent(Event event) {
		ArrayList<EventMatch> eventMatchList = this.actionDescriptionStore
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

		ArrayList<Mechanism> mechanismList = this.actionDescriptionStore
				.getMechanismList(event.getEventAction());
		if (mechanismList == null)
			mechanismList = new ArrayList<Mechanism>();
		log.debug(
				"Searching for triggered mechanisms for event=[{}] -> subscriptions: {}",
				event.getEventAction(), mechanismList.size());

		Decision d = new Decision(new AuthorizationAction("default", Constants.AUTHORIZATION_ALLOW));
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
			 for (IPdpMechanism m: this.policyTable.get(policyName)){
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

}
