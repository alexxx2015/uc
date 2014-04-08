package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods a PMP can invoke on a PMP.
 * @author Kelbert & Lovat
 *
 */
public interface IPmp2Pmp {
	/**
	 * Transfers the specified policies to this PMP, which
	 * will then 'manage' them and deploy them at the PDP.
	 *
	 * @param policies the transferred policies
	 * @return
	 */
	IStatus receivePolicies(Set<String> policies);
}
