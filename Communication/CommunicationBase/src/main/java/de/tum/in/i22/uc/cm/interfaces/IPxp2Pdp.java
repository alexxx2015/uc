package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PXP can invoke on a PDP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPxp2Pdp")
public interface IPxp2Pdp {
	@AThriftMethod(signature="bool registerPxp(1: Types.TPxpSpec pxp)")
	public boolean registerPxp(PxpSpec pxp);
}

