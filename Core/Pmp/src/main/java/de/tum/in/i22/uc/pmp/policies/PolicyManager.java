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

	private Map<String, XmlPolicy> policies;
	
	public PolicyManager(){
		policies = new HashMap<String, XmlPolicy>();
	}
	
	/**
	 * Adds a policy that was not before in the storage.
	 * I
	 * @param p
	 * @return true if it is unique. false if already exists.
	 */
	public boolean addPolicy(XmlPolicy p){
		if(p == null)
			return false;
		String policyName = p.getName();
		if(this.policies.containsKey(policyName))
			return false;
		this.policies.put(policyName, p);
		return true;
	}

	public void replacePolicy(XmlPolicy p){
		if(p==null)
			return;
		String policyName = p.getName();
		this.policies.put(policyName, p);
	}
	
	public boolean removePolicy(String name){
		if(name==null)
			return false;
		XmlPolicy p = this.policies.remove(name);
		if(p==null)
			return false;
		return true;
	}
	
	public XmlPolicy getPolicy(String name){
		if(name == null)
			return null;
		XmlPolicy p = this.policies.get(name);
		return p;
	}
	
	public Collection<XmlPolicy> getPolicies(){
		Collection<XmlPolicy> p = this.policies.values();
		return p;
	}
	
}
