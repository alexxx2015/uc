package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.thrift.generator.AThriftService;


/**
 * Interface defining methods to be invoked from a PIP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPip2Any")
public interface IPip2Any extends IPip2Pip,IPip2Pmp{

}
