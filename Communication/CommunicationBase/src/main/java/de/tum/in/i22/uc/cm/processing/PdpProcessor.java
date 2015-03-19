package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Dmp;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;

/**
 * An abstract PDP processor, extending {@link Processor} to take interfaces to
 * a {@link PipProcessor} and to a {@link PmpProcessor} as an argument.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PdpProcessor extends Processor<PipProcessor, PmpProcessor, IPdp2Dmp> implements IAny2Pdp {

	public PdpProcessor(Location location) {
		super(location);
	}

//	/**
//	 * Returns the PMP associated with this PDP.
//	 *
//	 * @return
//	 */
//	protected IPdp2Pmp getPmp() {
//		return _iface2;
//	}

	/**
	 * Returns the PIP associated with this PDP.
	 *
	 * @return
	 */
	protected IPdp2Pip getPip() {
		return _iface1;
	}
}
