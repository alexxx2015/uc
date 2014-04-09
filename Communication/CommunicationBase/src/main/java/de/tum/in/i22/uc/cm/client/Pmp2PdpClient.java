package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp;


public abstract class Pmp2PdpClient extends GenericX2YClient implements IPmp2Pdp {
	protected Pmp2PdpClient(Connector<?> connector) {
		super(connector);
	}
}
