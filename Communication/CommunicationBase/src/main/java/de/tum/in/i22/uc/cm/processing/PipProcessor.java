package de.tum.in.i22.uc.cm.server;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;

/**
 * An abstract PIP processor, extending {@link Processor} to take interfaces to
 * a {@link PdpProcessor} and to a {@link PmpProcessor} as an argument.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PipProcessor extends Processor<PdpProcessor,PmpProcessor> implements IAny2Pip {

	/**
	 * Returns the PMP associated with this PIP.
	 * @return
	 */
	protected IAny2Pmp getPmp() {
		return _iface2;
	}

	/**
	 * Returns the PDP associated with this PIP.
	 * @return
	 */
	protected IAny2Pdp getPdp() {
		return _iface1;
	}
}
