package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;

/**
 * An abstract PMP processor, extending {@link Processor} to take interfaces to
 * a {@link PipProcessor} and to a {@link PdpProcessor} as an argument.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PmpProcessor extends Processor<PipProcessor,PdpProcessor> implements IAny2Pmp {

	/**
	 * Returns the PDP associated with this PMP.
	 * @return
	 */
	protected IAny2Pdp getPdp() {
		return _iface2;
	}

	/**
	 * Returns the PIP associated with this PMP.
	 * @return
	 */
	protected IAny2Pip getPip() {
		return _iface1;
	}
}