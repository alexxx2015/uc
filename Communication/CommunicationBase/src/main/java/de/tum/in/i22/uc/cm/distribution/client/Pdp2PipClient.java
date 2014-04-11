package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;



public abstract class Pdp2PipClient extends GenericX2YClient implements IPdp2Pip {
	protected Pdp2PipClient(Connector<?> connector) {
		super(connector);
	}
}
