package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IPip2Dmp;

/**
 * An abstract PIP processor, extending {@link Processor} to take interfaces to
 * a {@link PdpProcessor} and to a {@link PmpProcessor} as an argument.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PipProcessor extends Processor<PdpProcessor,PmpProcessor,IPip2Dmp> implements IAny2Pip {

	public PipProcessor(Location location) {
		super(location);
	}
//
//	/**
//	 * Returns the PMP associated with this PIP.
//	 * @return
//	 */
//	protected IPip2Pmp getPmp() {
//		return _iface2;
//	}
//
//	/**
//	 * Returns the PDP associated with this PIP.
//	 * @return
//	 */
//	protected IPip2Pdp getPdp() {
//		return _iface1;
//	}
}
