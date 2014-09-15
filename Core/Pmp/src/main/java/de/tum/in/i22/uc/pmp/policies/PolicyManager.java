package de.tum.in.i22.uc.pmp.policies;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;

/**
 * @author cipri
 * For each policy that is translated,
 * this object retains the data necessary to retranslate,
 * redeploy and reinstantiate policies.
 * This is needed for the Adaptation Engine, part of the PTP.
 */
public class PolicyManager {

	private Map<String, XmlPolicy> _policies;

	public PolicyManager(){
		_policies = new HashMap<String, XmlPolicy>();
	}

	/**
	 * Adds a policy that was not before in the storage.
	 *
	 * @param p
	 * @return true if it is unique. false if already exists.
	 */
	public boolean addPolicy(XmlPolicy p){
		if (p == null) {
			return false;
		}
		String policyName = p.getName();
		if (_policies.containsKey(policyName)) {
			return false;
		}
		_policies.put(policyName, p);
		return true;
	}

	public void replacePolicy(XmlPolicy p){
		if (p == null) {
			return;
		}
		_policies.put(p.getName(), p);
	}

	public boolean removePolicy(String name){
		if (name == null) {
			return false;
		}
		XmlPolicy p = _policies.remove(name);

		return p != null;
	}

	public XmlPolicy getPolicy(String name){
		if (name == null) {
			return null;
		}
		return _policies.get(name);
	}

	public Collection<XmlPolicy> getPolicies(){
		return _policies.values();
	}

}
