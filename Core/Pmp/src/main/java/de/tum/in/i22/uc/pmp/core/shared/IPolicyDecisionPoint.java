package de.tum.in.i22.uc.pmp.core.shared;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.interfaces.IPmp2Pip;

public interface IPolicyDecisionPoint {
	// PDP exported methods
	public Decision notifyEvent(Event event);

	public boolean deployPolicyURI(String filename);

	public boolean deployPolicyXML(String XMLPolicy);

	public boolean revokePolicy(String policyName);

	public boolean revokeMechanism(String policyName, String mechName);

	public Map<String, List<String>> listDeployedMechanisms();
	
	public IPmp2Pip get_pip();

}
