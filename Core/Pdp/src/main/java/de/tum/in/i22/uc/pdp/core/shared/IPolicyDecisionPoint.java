package de.tum.in.i22.uc.pdp.core.shared;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.pdp.PxpManager;
import de.tum.in.i22.uc.pdp.core.ActionDescriptionStore;

public interface IPolicyDecisionPoint {
	public Decision notifyEvent(Event event);

	/**
	 * Deploys the specified policy file.
	 * @param filename the policy file to be deployed
	 * @return true, if deployment was successful
	 */
	public boolean deployPolicyURI(String filename);

	public boolean deployPolicyXML(XmlPolicy XMLPolicy);

	public boolean revokePolicy(String policyName);

	public boolean revokeMechanism(String policyName, String mechName);

	public Map<String, List<String>> listDeployedMechanisms();

	public IPdp2Pip getPip();

	public ActionDescriptionStore getActionDescriptionStore();

	public PxpManager getPxpManager();

	public void stop();

}
