package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Dmp;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Pip;

/**
 * An abstract PMP processor, extending {@link Processor} to take interfaces to
 * a {@link PipProcessor} and to a {@link PdpProcessor} as an argument.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PmpProcessor extends Processor<PipProcessor,PdpProcessor, IPmp2Dmp> implements IAny2Pmp {

	public PmpProcessor(Location location) {
		super(location);
	}

	/**
	 * Returns the PDP associated with this PMP.
	 * @return
	 */
	protected IPmp2Pdp getPdp() {
		return _iface2;
	}

	/**
	 * Returns the PIP associated with this PMP.
	 * @return
	 */
	protected IPmp2Pip getPip() {
		return _iface1;
	}
}