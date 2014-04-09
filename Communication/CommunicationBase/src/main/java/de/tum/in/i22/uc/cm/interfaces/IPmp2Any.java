package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.thrift.generator.AThriftService;


/**
 * Interface defining methods to be invoked from a PMP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPmp2Any")
public interface IPmp2Any extends IPmp2Pdp, IPmp2Pip, IPmp2Pmp {
}
