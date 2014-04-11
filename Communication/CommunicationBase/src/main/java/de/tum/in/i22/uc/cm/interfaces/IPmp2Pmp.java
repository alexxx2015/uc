package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PMP can invoke on a PMP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPmp2Pmp")
public interface IPmp2Pmp {
	/**
	 * Transfers the specified policies to this PMP, which
	 * will then 'manage' them and deploy them at the PDP.
	 *
	 * @param policies the transferred policies
	 * @return
	 */
	@AThriftMethod(signature="Types.TStatus remotePolicyTransfer(1: set<string> policies)")
	public IStatus receivePolicies(Set<String> policies);
}
