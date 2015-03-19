package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.distribution.IPLocation;

public interface IPmp2Dmp {
	/**
	 * Method to be invoked whenever a new policy name ought to be managed.
	 *
	 * @param policy the policy to be managed.
	 */	
	void register(XmlPolicy policy, String from);

	void deregister(String policyName, IPLocation location);
}
