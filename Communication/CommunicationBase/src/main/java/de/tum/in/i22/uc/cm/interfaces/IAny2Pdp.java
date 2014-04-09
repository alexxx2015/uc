package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.thrift.generator.AThriftService;


/**
 * Interface defining methods to be invoked on a PDP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TAny2Pdp")
public interface IAny2Pdp extends IPep2Pdp, IPxp2Pdp, IPmp2Pdp {

}
