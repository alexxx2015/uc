package de.tum.in.i22.uc.pmp.policies;

/**
 * @author cipri
 * Retains the name of the policy,
 * it's template before translation,
 * it's instantiated template,
 * the translated policy.
 */
public class PolicyObject {

	private String policyName;
	private String policyId;
	
	public PolicyObject(String id, String name){
		this.policyId = id;
		this.policyName = name;
	}
	
	public String getName(){
		return this.policyName;
	}
	
	public String getId(){
		return this.policyId;
	}
	
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(!(o instanceof PolicyObject))
			return false;
		PolicyObject p = (PolicyObject) o;
		if(!this.policyId.equals(p.policyId))
			return false;
		if(!this.policyName.equals(p.policyName))
			return false;
		return true;
	}
	
	public String toString(){
		String result = "";
		result += this.policyId +"#"+ this.policyName;
		return result;
	}
	
	@Override
	public int hashCode() {
		return policyName.hashCode();
	}
}
