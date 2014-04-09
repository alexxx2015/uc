package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods to be invoked from a PEP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPep2Any")
public interface IPep2Any extends IPep2Pdp,IPep2Pip{

}
