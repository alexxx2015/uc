package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.thrift.generator.AThriftService;


/**
 * Interface defining methods to be invoked on a PIP.
 *
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name = "TAny2Pip")
public interface IAny2Pip extends IPdp2Pip, IPep2Pip, IPip2Pip, IPmp2Pip, IPxp2Pip {
}
