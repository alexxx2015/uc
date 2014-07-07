package de.tum.in.i22.uc.gui;

public class DeployedMechanism {
	private String policy;
	private String mechanism;
	
	public DeployedMechanism(String p_policy, String p_mechanism){
		this.policy = p_policy;
		this.mechanism = p_mechanism;
	}
	
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	public String getMechanism() {
		return mechanism;
	}
	public void setMechanism(String mechanism) {
		this.mechanism = mechanism;
	}
	
}
