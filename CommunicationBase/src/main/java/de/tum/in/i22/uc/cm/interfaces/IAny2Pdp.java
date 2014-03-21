package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.requests.PdpRequest;

/**
 * Incoming interface. Other modules use this interface to invoke operations
 * on PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pdp extends IPmp2Pdp, IPep2Pdp, IPip2Pdp, IPdp2Pdp, IPxp2Pdp, IAny2Generic<IAny2Pip,IAny2Pmp>  {

}
