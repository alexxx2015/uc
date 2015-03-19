package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.thrift.generator.AThriftService;


/**
 * Interface defining methods to be invoked on a DMP.
 * @author Florian Kelbert
 *
 */
@AThriftService(name="TAny2Dmp")
public interface IAny2Dmp extends IDmp2Dmp, IPdp2Dmp, IPip2Dmp, IPmp2Dmp {

}
