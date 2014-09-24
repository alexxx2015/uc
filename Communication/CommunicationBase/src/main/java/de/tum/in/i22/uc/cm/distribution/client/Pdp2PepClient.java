package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IPdp2Pep;



public abstract class Pdp2PepClient extends GenericX2YClient implements IPdp2Pep {
	protected Pdp2PepClient(Connector<?> connector) {
		super(connector);
	}
}
