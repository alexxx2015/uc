package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;


public abstract class Pep2PdpClient extends GenericX2YClient implements IPep2Pdp {
	protected Pep2PdpClient(Connector<?> connector) {
		super(connector);
	}
}
