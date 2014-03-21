package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.requests.PmpRequest;

/**
 * Incoming interface. Other modules use this interface to invoke operations
 * on PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pmp extends IPdp2Pmp, IPip2Pmp, IPmp2Pmp {
	void init(IAny2Pip pip, IAny2Pdp pdp);
	Object process(PmpRequest request);
}
