package de.tum.in.i22.uc.pmp.policies;

import java.util.ArrayList;
import java.util.HashMap;
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
	/**
	 * The parameters needed for each policy to retranslate and redeploy.
	 * e.g. scope of the request; modifications to policy specific to application
	 */
	private Map<String, Map<String,String>> _params;

	public PolicyManager(){
		_policies = new ConcurrentHashMap<>();
		_params = new ConcurrentHashMap<>();
	}

	/**
	 * Adds a policy that was not before in the storage.
	 * <br> Adds a policy on the PMP's list of deployed policies.
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

	/**
	 * Adds instantiation parameters for a policy.
	 * If the parameters were already stored, they are replaced with ones as argument.
	 * @param p
	 * @param param
	 * @return
	 */
	public boolean addPolicyParam(XmlPolicy p, Map<String, String> param){
		if(p == null || param == null ){
			return false;
		}
		String policyName = p.getName();
		this._params.put(policyName, param);
		return true;
	}

	/**
	 * Returns the parameters for a particular policy.
	 * <br> The policy is identified by its unique name.
	 * If the policy is null or it does not have parameters,
	 * it returns an empty parameters map.
	 * @param p
	 * @return
	 */
	public Map<String, String> getPolicyParam(XmlPolicy p){
		if(p==null){
			return new HashMap<String,String>();
		}
		String policyName = p.getName();
		Map<String, String> par = this._params.get(policyName);
		if(par==null)
			return new HashMap<String,String>();
		return par;
	}

	/**
	 * Remove the policy from the PMP's list
	 * @param p
	 */
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
