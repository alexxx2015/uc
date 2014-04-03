package de.tum.in.i22.uc.cm.interfaces;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods to be invoked on a PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pdp {

	/*
	 * From PEP
	 */
	public void notifyEventAsync(IEvent event);
	public IResponse notifyEventSync(IEvent event);

	/*
	 * From PMP
	 */
	public IMechanism exportMechanism(String par);
	public IStatus revokePolicy(String policyName);
	public IStatus revokeMechanism(String policyName, String mechName);
	public IStatus deployPolicyURI(String policyFilePath);
	public IStatus deployPolicyXML(String XMLPolicy);
	public Map<String, List<String>> listMechanisms();

	/*
	 * From PXP
	 */
	public boolean registerPxp(IPxpSpec pxp);
}
