package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.basic.PxpSpec;

/**
 * Interface defining methods a PXP can invoke on a PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IPxp2Pdp {
	public boolean registerPxp(PxpSpec pxp);
}

