package de.tum.in.i22.uc.cm.interfaces;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods a PMP can invoke on a PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IPmp2Pdp {
	public IMechanism exportMechanism(String par);
	public IStatus revokePolicy(String policyName);
	public IStatus revokeMechanism(String policyName, String mechName);
	public IStatus deployPolicyURI(String policyFilePath);
	public IStatus deployPolicyXML(String XMLPolicy);
	public Map<String, List<String>> listMechanisms();

}
