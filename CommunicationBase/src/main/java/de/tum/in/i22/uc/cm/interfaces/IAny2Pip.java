package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.requests.PipRequest;

/**
 * Incoming interface. Other modules use this interface to invoke operations
 * on PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pip extends IPdp2Pip, IPip2Pip, IPmp2Pip, IPep2Pip {
	void init(IAny2Pdp pdp, IAny2Pmp pmp);
	Object process(PipRequest request);
}
