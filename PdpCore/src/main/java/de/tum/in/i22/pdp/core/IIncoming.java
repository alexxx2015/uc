package de.tum.in.i22.pdp.core;

import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IPip2Pdp;

/**
 * Incoming interface. Other modules use this interface to invoke operations
 * on PDP.
 * @author Stoimenov
 *
 */
public interface IIncoming extends IPmp2Pdp, IPep2Pdp, IPip2Pdp, IPdpConf{

}
