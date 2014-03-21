package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.requests.PdpRequest;

/**
 * Incoming interface. Other modules use this interface to invoke operations
 * on PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pdp extends IPmp2Pdp, IPep2Pdp, IPip2Pdp, IPdp2Pdp, IPxp2Pdp {
	void init(IAny2Pip pip, IAny2Pmp pmp);
	Object process(PdpRequest request);
}
