package de.tum.in.i22.uc.pmp.policies;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cipri
 * For each policy that is translated, 
 * this object retains the data necessary to retranslate,
 * redeploy and reinstantiate policies. 
 * This is needed for the Adaptation Engine, part of the PTP.
 */
public class PolicyManager {

	private Map<String, PolicyObject> policies;
	
	public PolicyManager(){
		policies = new HashMap<String, PolicyObject>();
	}
	
	public void addPolicy(PolicyObject p){
		String policyName = p.getName();
		this.policies.put(policyName, p);
	}
	
	public PolicyObject getPolicy(String name){
		PolicyObject p = this.policies.get(name);
		return p;
	}
}
