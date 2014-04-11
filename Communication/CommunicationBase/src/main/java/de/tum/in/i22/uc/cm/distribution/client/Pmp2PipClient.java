package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IPmp2Pip;


public abstract class Pmp2PipClient extends GenericX2YClient implements IPmp2Pip {
	protected Pmp2PipClient(Connector<?> connector) {
		super(connector);
	}
}
