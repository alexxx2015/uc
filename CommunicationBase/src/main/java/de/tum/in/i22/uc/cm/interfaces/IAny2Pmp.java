package de.tum.in.i22.uc.cm.interfaces;


/**
 * Incoming interface. Other modules use this interface to invoke operations
 * on PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pmp extends IPdp2Pmp, IPip2Pmp, IPmp2Pmp, IAny2Generic<IAny2Pip, IAny2Pdp>  {

}
