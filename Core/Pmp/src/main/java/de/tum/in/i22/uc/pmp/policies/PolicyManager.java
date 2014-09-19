package de.tum.in.i22.uc.pmp.policies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
		_policies = new ConcurrentHashMap<String, XmlPolicy>(50);
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

	/**
	 * Returns a snapshot of the store values.
	 * iterates over the elements and creates a list with the values.
	 * @return
	 */
	public List<XmlPolicy> getPolicies(){
		List<XmlPolicy> values = new ArrayList<>();
		
		Iterator<Entry<String, XmlPolicy>> iterator = _policies.entrySet().iterator();
		while(iterator.hasNext()){
			XmlPolicy p = iterator.next().getValue();
			values.add(p);
		}
		return values;
	}

}
