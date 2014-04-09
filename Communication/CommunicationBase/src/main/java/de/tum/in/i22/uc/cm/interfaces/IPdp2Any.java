package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods to be invoked from a PDP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPdp2Any")
public interface IPdp2Any extends IPdp2Pip,IPdp2Pxp{

}
